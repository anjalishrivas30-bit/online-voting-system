package com.internship.voting.service;

import com.internship.voting.dto.CandidateForm;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.Candidate;
import com.internship.voting.model.Election;
import com.internship.voting.repository.CandidateRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Candidate management (admin). Candidates can only be changed while the election is still modifiable. */
@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;
    private final AuditService auditService;

    public CandidateService(CandidateRepository candidateRepository, ElectionService electionService,
                            AuditService auditService) {
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Candidate> findAll() {
        return candidateRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Candidate> findByElection(Long electionId) {
        return candidateRepository.findByElectionIdOrderByNameAsc(electionId);
    }

    /** Admin search: optional election filter + text that matches name, party or description. */
    @Transactional(readOnly = true)
    public List<Candidate> search(Long electionId, String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Candidate> base = electionId == null ? findAll() : findByElection(electionId);
        return base.stream()
                .filter(c -> q.isEmpty()
                        || c.getName().toLowerCase().contains(q)
                        || c.getParty().toLowerCase().contains(q)
                        || (c.getDescription() != null && c.getDescription().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Candidate getById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }

    @Transactional(readOnly = true)
    public long count() {
        return candidateRepository.count();
    }

    @Transactional
    public Candidate create(CandidateForm form, String actor) {
        Election election = electionService.getById(form.getElectionId());
        electionService.assertModifiable(election);
        Candidate candidate = new Candidate();
        copyFields(candidate, form);
        candidate.setElection(election);
        Candidate saved = candidateRepository.save(candidate);
        auditService.log(actor, "CANDIDATE_ADDED", "Candidate " + saved.getName() + " added to " + election.getTitle());
        return saved;
    }

    /** The election of an existing candidate cannot be changed - delete and re-create instead. */
    @Transactional
    public Candidate update(Long id, CandidateForm form, String actor) {
        Candidate candidate = getById(id);
        electionService.assertModifiable(candidate.getElection());
        copyFields(candidate, form);
        Candidate saved = candidateRepository.save(candidate);
        auditService.log(actor, "CANDIDATE_UPDATED", "Candidate updated: " + saved.getName());
        return saved;
    }

    @Transactional
    public void delete(Long id, String actor) {
        Candidate candidate = getById(id);
        electionService.assertModifiable(candidate.getElection());
        candidateRepository.delete(candidate);
        auditService.log(actor, "CANDIDATE_DELETED", "Candidate deleted: " + candidate.getName());
    }

    private void copyFields(Candidate candidate, CandidateForm form) {
        candidate.setName(form.getName().trim());
        candidate.setParty(form.getParty().trim());
        candidate.setPosition(form.getPosition() == null || form.getPosition().isBlank() ? "Candidate" : form.getPosition().trim());
        candidate.setDescription(form.getDescription() == null ? null : form.getDescription().trim());
    }
}
