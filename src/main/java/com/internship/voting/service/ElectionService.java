package com.internship.voting.service;

import com.internship.voting.dto.ElectionForm;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import com.internship.voting.repository.CandidateRepository;
import com.internship.voting.repository.ElectionRepository;
import com.internship.voting.repository.VoteRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Election management (admin) and automatic status updates. */
@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final AuditService auditService;

    public ElectionService(ElectionRepository electionRepository, CandidateRepository candidateRepository,
                           VoteRepository voteRepository, AuditService auditService) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voteRepository = voteRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Election> findAll() {
        return electionRepository.findAllByOrderByStartTimeDesc();
    }

    /** @param status null = all elections. Uses the live (clock based) status. */
    @Transactional(readOnly = true)
    public List<Election> findByStatus(ElectionStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return findAll().stream()
                .filter(e -> status == null || e.computeStatus(now) == status)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return findByStatus(ElectionStatus.ACTIVE).size();
    }

    @Transactional(readOnly = true)
    public Election getById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
    }

    @Transactional(readOnly = true)
    public long count() {
        return electionRepository.count();
    }

    @Transactional
    public Election create(ElectionForm form, String actor) {
        validateTimes(form.getStartTime(), form.getEndTime());
        Election election = new Election();
        apply(election, form);
        Election saved = electionRepository.save(election);
        auditService.log(actor, "ELECTION_CREATED", "Election created: " + saved.getTitle());
        return saved;
    }

    @Transactional
    public Election update(Long id, ElectionForm form, String actor) {
        Election election = getById(id);
        assertModifiable(election);
        validateTimes(form.getStartTime(), form.getEndTime());
        apply(election, form);
        Election saved = electionRepository.save(election);
        auditService.log(actor, "ELECTION_UPDATED", "Election updated: " + saved.getTitle());
        return saved;
    }

    @Transactional
    public void delete(Long id, String actor) {
        Election election = getById(id);
        if (voteRepository.countByElectionId(id) > 0) {
            throw new BusinessException("This election already has votes and cannot be deleted.");
        }
        candidateRepository.deleteByElectionId(id);
        electionRepository.delete(election);
        auditService.log(actor, "ELECTION_DELETED", "Election deleted: " + election.getTitle());
    }

    /** Ends an active election immediately by moving its end time to "now". */
    @Transactional
    public void closeNow(Long id, String actor) {
        Election election = getById(id);
        LocalDateTime now = LocalDateTime.now();
        if (election.computeStatus(now) != ElectionStatus.ACTIVE) {
            throw new BusinessException("Only an active election can be closed early.");
        }
        election.setEndTime(now);
        election.setStatus(ElectionStatus.CLOSED);
        electionRepository.save(election);
        auditService.log(actor, "ELECTION_CLOSED", "Election closed early: " + election.getTitle());
    }

    /**
     * Fairness rule: an election (and its candidate list) can only be changed while it is not closed
     * and while no vote has been cast. Changing the ballot after voting started would be unfair.
     */
    @Transactional(readOnly = true)
    public void assertModifiable(Election election) {
        if (election.computeStatus(LocalDateTime.now()) == ElectionStatus.CLOSED) {
            throw new BusinessException("This election is closed and can no longer be modified.");
        }
        if (voteRepository.countByElectionId(election.getId()) > 0) {
            throw new BusinessException("Votes have already been cast, so this election can no longer be modified.");
        }
    }

    /** Called by the scheduler every minute: keeps the stored status column in sync with the clock. */
    @Transactional
    public int refreshStatuses() {
        LocalDateTime now = LocalDateTime.now();
        int changed = 0;
        for (Election election : electionRepository.findAll()) {
            ElectionStatus computed = election.computeStatus(now);
            if (computed != election.getStatus()) {
                election.setStatus(computed);
                electionRepository.save(election);
                changed++;
            }
        }
        return changed;
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new BusinessException("End time must be after the start time.");
        }
        if (!end.isAfter(LocalDateTime.now())) {
            throw new BusinessException("End time must be in the future.");
        }
    }

    private void apply(Election election, ElectionForm form) {
        election.setTitle(form.getTitle().trim());
        election.setDescription(form.getDescription() == null ? null : form.getDescription().trim());
        election.setStartTime(form.getStartTime());
        election.setEndTime(form.getEndTime());
        election.setStatus(election.computeStatus(LocalDateTime.now()));
    }
}
