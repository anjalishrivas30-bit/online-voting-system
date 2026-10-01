package com.internship.voting.repository;

import com.internship.voting.model.Election;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findAllByOrderByStartTimeDesc();
}
