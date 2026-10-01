package com.internship.voting.service;

import com.internship.voting.dto.RegistrationDto;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.exception.ResourceNotFoundException;
import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.repository.RoleRepository;
import com.internship.voting.repository.UserRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Voter registration and admin approval workflow. */
@Service
public class UserService {

    private static final int MINIMUM_VOTING_AGE = 18;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    /**
     * Registers a new voter. Business rules:
     *  1. passwords must match, 2. voter must be 18+, 3. email and voter id must be unique.
     * The password is hashed with BCrypt (salted, slow) before it is saved; the new voter starts as PENDING
     * and cannot vote until an admin approves the registration.
     */
    @Transactional
    public User registerVoter(RegistrationDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match.");
        }
        int age = Period.between(dto.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < MINIMUM_VOTING_AGE) {
            throw new BusinessException("You must be at least " + MINIMUM_VOTING_AGE + " years old to register.");
        }

        String email = dto.getEmail().trim().toLowerCase();
        String voterId = dto.getVoterId().trim().toUpperCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("An account with this email already exists.");
        }
        if (userRepository.existsByVoterId(voterId)) {
            throw new BusinessException("This voter ID is already registered.");
        }

        Role voterRole = roleRepository.findByName(Role.VOTER)
                .orElseThrow(() -> new BusinessException("Voter role is not configured in the database."));

        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(email);
        user.setVoterId(voterId);
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(voterRole);
        user.setStatus(VoterStatus.PENDING);
        User saved = userRepository.save(user);

        auditService.log(email, "VOTER_REGISTERED", "New voter registration, voter ID " + voterId);
        return saved;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** All voters, pending registrations first, then newest first. */
    @Transactional(readOnly = true)
    public List<User> findAllVoters() {
        List<User> voters = new ArrayList<>(userRepository.findByRoleName(Role.VOTER));
        voters.sort(Comparator.comparing((User u) -> u.getStatus() != VoterStatus.PENDING)
                .thenComparing(Comparator.comparing(User::getCreatedAt).reversed()));
        return voters;
    }

    /** Admin search: text matches name, email or voter ID; status is optional (null = any). */
    @Transactional(readOnly = true)
    public List<User> searchVoters(String query, VoterStatus status) {
        String q = query == null ? "" : query.trim().toLowerCase();
        return findAllVoters().stream()
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> q.isEmpty()
                        || u.getFullName().toLowerCase().contains(q)
                        || u.getEmail().toLowerCase().contains(q)
                        || u.getVoterId().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> findPendingVoters() {
        return userRepository.findByRoleNameAndStatus(Role.VOTER, VoterStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public long countVoters() {
        return userRepository.countByRoleName(Role.VOTER);
    }

    @Transactional(readOnly = true)
    public long countByStatus(VoterStatus status) {
        return userRepository.countByRoleNameAndStatus(Role.VOTER, status);
    }

    /** Admin action: approve or reject a voter registration. */
    @Transactional
    public void updateVoterStatus(Long userId, VoterStatus newStatus, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));
        if (!Role.VOTER.equals(user.getRole().getName())) {
            throw new BusinessException("Only voter accounts can be approved or rejected.");
        }
        user.setStatus(newStatus);
        userRepository.save(user);
        auditService.log(adminEmail, "VOTER_" + newStatus.name(),
                "Voter " + user.getVoterId() + " (" + user.getEmail() + ") marked " + newStatus.name());
    }
}
