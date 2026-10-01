package com.internship.voting.service;

import com.internship.voting.exception.BusinessException;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.RequestType;
import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.VoterRequest;
import com.internship.voting.repository.UserRepository;
import com.internship.voting.repository.VoterRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Voter request system: a voter submits a request, an admin approves or rejects it with a remark,
 * and the voter sees the new status (plus a simple in-app notification).
 */
@Service
public class RequestService {

    private static final int MAX_REMARK_LENGTH = 500;

    private final VoterRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RequestService(VoterRequestRepository requestRepository, UserRepository userRepository,
                          AuditService auditService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    /** A voter submits a new request. It always starts as PENDING. */
    @Transactional
    public VoterRequest submit(String email, RequestType type, String description) {
        User voter = loadUser(email);
        if (voter.getRole() == null || !Role.VOTER.equals(voter.getRole().getName())) {
            throw new BusinessException("Only voters can submit requests.");
        }
        if (type == null) {
            throw new BusinessException("Please choose a request type.");
        }
        if (description == null || description.isBlank()) {
            throw new BusinessException("Please describe your request.");
        }
        VoterRequest saved = requestRepository.save(new VoterRequest(voter, type, description.trim()));
        auditService.log(voter.getEmail(), "REQUEST_SUBMITTED", type.getLabel() + " (request #" + saved.getId() + ")");
        return saved;
    }

    /**
     * Admin decision. Business rules:
     *  - only a PENDING request can be decided (no changing a decision later),
     *  - a rejection must have a remark so the voter knows why.
     * The voter is "notified" by clearing the voterNotified flag.
     */
    @Transactional
    public void decide(Long requestId, RequestStatus newStatus, String remark, String adminEmail) {
        VoterRequest request = getById(requestId);
        if (newStatus == null || newStatus == RequestStatus.PENDING) {
            throw new BusinessException("Choose Approve or Reject.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("This request has already been " + request.getStatus().getLabel().toLowerCase() + ".");
        }
        String cleanRemark = remark == null ? "" : remark.trim();
        if (newStatus == RequestStatus.REJECTED && cleanRemark.isEmpty()) {
            throw new BusinessException("Please add a short remark explaining the rejection.");
        }
        if (cleanRemark.length() > MAX_REMARK_LENGTH) {
            throw new BusinessException("Remark must be at most " + MAX_REMARK_LENGTH + " characters.");
        }
        request.setStatus(newStatus);
        request.setAdminRemark(cleanRemark.isEmpty() ? null : cleanRemark);
        request.setDecidedAt(LocalDateTime.now());
        request.setVoterNotified(false);
        requestRepository.save(request);
        auditService.log(adminEmail, "REQUEST_" + newStatus.name(),
                "Request #" + request.getId() + " from " + request.getVoter().getEmail() + " " + newStatus.getLabel().toLowerCase());
    }

    @Transactional(readOnly = true)
    public VoterRequest getById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
    }

    @Transactional(readOnly = true)
    public List<VoterRequest> findForVoter(String email) {
        return requestRepository.findByVoterIdOrderByCreatedAtDesc(loadUser(email).getId());
    }

    /** @param status null = all requests */
    @Transactional(readOnly = true)
    public List<VoterRequest> findAll(RequestStatus status) {
        return status == null
                ? requestRepository.findAllByOrderByCreatedAtDesc()
                : requestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public long countByStatus(RequestStatus status) {
        return requestRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return requestRepository.count();
    }

    /** Decided requests the voter has not seen yet -> shown as notifications. */
    @Transactional(readOnly = true)
    public List<VoterRequest> unseenDecisions(String email) {
        return requestRepository.findByVoterIdAndVoterNotifiedFalseOrderByDecidedAtDesc(loadUser(email).getId());
    }

    @Transactional
    public void markSeen(String email) {
        for (VoterRequest request : unseenDecisions(email)) {
            request.setVoterNotified(true);
            requestRepository.save(request);
        }
    }

    @Transactional(readOnly = true)
    public long countUnseenFor(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .map(user -> requestRepository.countByVoterIdAndVoterNotifiedFalse(user.getId()))
                .orElse(0L);
    }

    private User loadUser(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
