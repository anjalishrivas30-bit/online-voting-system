package com.internship.voting.service;

import com.internship.voting.dto.VoteEligibility;
import com.internship.voting.dto.VoterElectionView;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.Candidate;
import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.Vote;
import com.internship.voting.model.VoterElectionStatus;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.repository.CandidateRepository;
import com.internship.voting.repository.ElectionRepository;
import com.internship.voting.repository.UserRepository;
import com.internship.voting.repository.VoteRepository;
import com.internship.voting.repository.VoterElectionStatusRepository;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The heart of the system: casting a vote.
 *
 * Three layers stop double voting:
 *   1. an application check (existsByVoterIdAndElectionId),
 *   2. a UNIQUE(voter_id, election_id) constraint in MySQL (wins even if two requests race),
 *   3. @Transactional - the "has voted" row and the vote row are saved together or not at all.
 */
@Service
public class VoteService {

    static final String ALREADY_VOTED = "You have already voted in this election.";

    private final UserRepository userRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final VoterElectionStatusRepository voterElectionStatusRepository;
    private final AuditService auditService;

    public VoteService(UserRepository userRepository, ElectionRepository electionRepository,
                       CandidateRepository candidateRepository, VoteRepository voteRepository,
                       VoterElectionStatusRepository voterElectionStatusRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.voterElectionStatusRepository = voterElectionStatusRepository;
        this.auditService = auditService;
    }

    /** Casts one vote. Everything happens in ONE database transaction. */
    @Transactional
    public void castVote(String email, Long electionId, Long candidateId) {
        User voter = loadVoter(email);
        assertApprovedVoter(voter);

        Election election = loadElection(electionId);
        assertActive(election);

        Candidate candidate = loadCandidate(electionId, candidateId);

        // Layer 1: friendly application-level check
        if (voterElectionStatusRepository.existsByVoterIdAndElectionId(voter.getId(), electionId)) {
            throw new BusinessException(ALREADY_VOTED);
        }

        // Layer 2: the unique constraint. saveAndFlush forces the INSERT now, so a concurrent duplicate
        // fails right here and is converted to a clean message.
        try {
            voterElectionStatusRepository.saveAndFlush(new VoterElectionStatus(voter, election));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ALREADY_VOTED);
        }

        // The ballot itself: no link to the voter (ballot secrecy).
        voteRepository.save(new Vote(election, candidate));

        // Audit: who voted and where - but NOT the choice.
        auditService.log(voter.getEmail(), "VOTE_CAST", "Vote cast in election: " + election.getTitle());
    }

    /** Used by the vote page: makes sure this voter may vote in this election right now. */
    @Transactional(readOnly = true)
    public Election assertEligible(String email, Long electionId) {
        User voter = loadVoter(email);
        assertApprovedVoter(voter);
        Election election = loadElection(electionId);
        assertActive(election);
        if (voterElectionStatusRepository.existsByVoterIdAndElectionId(voter.getId(), electionId)) {
            throw new BusinessException(ALREADY_VOTED);
        }
        return election;
    }

    /** Used by the confirmation page: same checks, plus the chosen candidate must belong to the election. */
    @Transactional(readOnly = true)
    public Candidate previewVote(String email, Long electionId, Long candidateId) {
        assertEligible(email, electionId);
        return loadCandidate(electionId, candidateId);
    }

    /**
     * SMART ELIGIBILITY CHECK (backend business logic, not just a button state):
     * upcoming -> "not started", completed -> "ended", active + unapproved -> "not approved",
     * active + already voted -> "already voted", otherwise "Vote Now".
     */
    @Transactional(readOnly = true)
    public VoterElectionView describe(User voter, Election election) {
        boolean voted = voterElectionStatusRepository.existsByVoterIdAndElectionId(voter.getId(), election.getId());
        ElectionStatus status = election.computeStatus(LocalDateTime.now());
        VoteEligibility eligibility;
        if (status == ElectionStatus.UPCOMING) {
            eligibility = VoteEligibility.NOT_STARTED;
        } else if (status == ElectionStatus.CLOSED) {
            eligibility = VoteEligibility.ENDED;
        } else if (voter.getStatus() != VoterStatus.APPROVED) {
            eligibility = VoteEligibility.NOT_APPROVED;
        } else if (voted) {
            eligibility = VoteEligibility.ALREADY_VOTED;
        } else {
            eligibility = VoteEligibility.CAN_VOTE;
        }
        return new VoterElectionView(election, voted, eligibility);
    }

    /** In how many elections this voter has already voted. */
    @Transactional(readOnly = true)
    public long countElectionsVoted(Long voterId) {
        return voterElectionStatusRepository.countByVoterId(voterId);
    }

    @Transactional(readOnly = true)
    public boolean hasVoted(Long voterId, Long electionId) {
        return voterElectionStatusRepository.existsByVoterIdAndElectionId(voterId, electionId);
    }

    @Transactional(readOnly = true)
    public long totalVotesCast() {
        return voteRepository.count();
    }

    // ------------------------------------------------------------------ helpers

    private User loadVoter(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Voter account not found."));
    }

    private void assertApprovedVoter(User voter) {
        if (voter.getRole() == null || !Role.VOTER.equals(voter.getRole().getName())) {
            throw new BusinessException("Only registered voters can vote.");
        }
        if (voter.getStatus() != VoterStatus.APPROVED) {
            throw new BusinessException("Your registration has not been approved yet, so you cannot vote.");
        }
    }

    private Election loadElection(Long electionId) {
        return electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
    }

    /** Uses the clock, not the stored status column, so a vote is never accepted after the end time. */
    private void assertActive(Election election) {
        ElectionStatus status = election.computeStatus(LocalDateTime.now());
        if (status == ElectionStatus.UPCOMING) {
            throw new BusinessException("Voting has not started yet.");
        }
        if (status == ElectionStatus.CLOSED) {
            throw new BusinessException("Voting has ended.");
        }
    }

    private Candidate loadCandidate(Long electionId, Long candidateId) {
        if (candidateId == null) {
            throw new BusinessException("Please select a candidate.");
        }
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new BusinessException("The selected candidate does not exist."));
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new BusinessException("The selected candidate does not belong to this election.");
        }
        return candidate;
    }
}
