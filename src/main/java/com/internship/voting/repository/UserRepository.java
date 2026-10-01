package com.internship.voting.repository;

import com.internship.voting.model.User;
import com.internship.voting.model.VoterStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Only derived queries are used: Spring Data turns them into PreparedStatements with bound parameters,
 * which is what protects the application from SQL injection.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByVoterId(String voterId);

    List<User> findByRoleName(String roleName);

    List<User> findByRoleNameAndStatus(String roleName, VoterStatus status);

    long countByRoleName(String roleName);

    long countByRoleNameAndStatus(String roleName, VoterStatus status);
}
