package com.internship.voting.repository;

import com.internship.voting.model.VoterElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterElectionStatusRepository extends JpaRepository<VoterElectionStatus, Long> {

    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);

    long countByElectionId(Long electionId);

    /** In how many elections has this voter already voted? */
    long countByVoterId(Long voterId);
}
