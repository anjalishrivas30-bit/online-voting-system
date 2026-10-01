package com.internship.voting.repository;

import com.internship.voting.model.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countByElectionId(Long electionId);

    /** Vote counting: one row per candidate that has votes -> [candidateId, number of votes]. */
    @Query("select v.candidate.id, count(v) from Vote v where v.election.id = :electionId group by v.candidate.id")
    List<Object[]> countVotesByCandidate(@Param("electionId") Long electionId);
}
