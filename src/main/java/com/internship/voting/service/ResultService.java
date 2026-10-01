package com.internship.voting.service;

import com.internship.voting.dto.CandidateResult;
import com.internship.voting.dto.ElectionResult;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.Candidate;
import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import com.internship.voting.model.Role;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.repository.CandidateRepository;
import com.internship.voting.repository.ElectionRepository;
import com.internship.voting.repository.UserRepository;
import com.internship.voting.repository.VoteRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Vote counting: totals per candidate, percentage, winner(s) and voter turnout. */
@Service
public class ResultService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public ResultService(ElectionRepository electionRepository, CandidateRepository candidateRepository,
                         VoteRepository voteRepository, UserRepository userRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param adminView true for admins: they may see live results while the election is still running.
     *                  Voters only get results after the election is CLOSED.
     */
    @Transactional(readOnly = true)
    public ElectionResult getResults(Long electionId, boolean adminView) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        boolean closed = election.computeStatus(LocalDateTime.now()) == ElectionStatus.CLOSED;
        if (!closed && !adminView) {
            throw new BusinessException("Results will be published once the election is closed.");
        }

        // One GROUP BY query gives votes per candidate; candidates with zero votes are added below.
        Map<Long, Long> votesByCandidate = new HashMap<>();
        for (Object[] row : voteRepository.countVotesByCandidate(electionId)) {
            votesByCandidate.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        List<Candidate> candidates = candidateRepository.findByElectionIdOrderByNameAsc(electionId);
        long totalVotes = 0;
        long maxVotes = 0;
        for (Candidate candidate : candidates) {
            long votes = votesByCandidate.getOrDefault(candidate.getId(), 0L);
            totalVotes += votes;
            maxVotes = Math.max(maxVotes, votes);
        }

        List<CandidateResult> rows = new ArrayList<>();
        List<String> winners = new ArrayList<>();
        for (Candidate candidate : candidates) {
            long votes = votesByCandidate.getOrDefault(candidate.getId(), 0L);
            double percentage = totalVotes == 0 ? 0 : Math.round(votes * 10000.0 / totalVotes) / 100.0;
            boolean isWinner = maxVotes > 0 && votes == maxVotes;
            if (isWinner) {
                winners.add(candidate.getName());
            }
            rows.add(new CandidateResult(candidate, votes, percentage, isWinner));
        }
        // Highest votes first
        rows.sort((a, b) -> Long.compare(b.getVotes(), a.getVotes()));

        long eligible = userRepository.countByRoleNameAndStatus(Role.VOTER, VoterStatus.APPROVED);
        double turnout = eligible == 0 ? 0 : Math.min(100.0, Math.round(totalVotes * 10000.0 / eligible) / 100.0);

        return new ElectionResult(election, rows, totalVotes, eligible, turnout, winners, closed);
    }
}
