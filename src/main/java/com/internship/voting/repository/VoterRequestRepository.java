package com.internship.voting.repository;

import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.VoterRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRequestRepository extends JpaRepository<VoterRequest, Long> {

    List<VoterRequest> findAllByOrderByCreatedAtDesc();

    List<VoterRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<VoterRequest> findByVoterIdOrderByCreatedAtDesc(Long voterId);

    /** Requests the admin has decided but the voter has not seen yet (in-app notification). */
    List<VoterRequest> findByVoterIdAndVoterNotifiedFalseOrderByDecidedAtDesc(Long voterId);

    long countByStatus(RequestStatus status);

    long countByVoterIdAndVoterNotifiedFalse(Long voterId);
}
