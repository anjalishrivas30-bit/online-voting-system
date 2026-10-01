package com.internship.voting.repository;

import com.internship.voting.model.Candidate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findAllByOrderByNameAsc();

    List<Candidate> findByElectionIdOrderByNameAsc(Long electionId);

    void deleteByElectionId(Long electionId);
}
