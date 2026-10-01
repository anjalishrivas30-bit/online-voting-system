package com.internship.voting.config;

import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.repository.RoleRepository;
import com.internship.voting.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once at every startup and GUARANTEES that the two roles and the default admin account exist,
 * even if data.sql did not run. The admin password hash is produced by the application's own BCrypt encoder,
 * so a login problem caused by a bad seed hash is impossible.
 *
 * Default admin: admin@voting.com / Admin@123
 * Set app.admin.reset-password=false in application.properties if you do not want the password reset at startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String ADMIN_EMAIL = "admin@voting.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.reset-password:true}")
    private boolean resetAdminPassword;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Role.ADMIN)));
        roleRepository.findByName(Role.VOTER)
                .orElseGet(() -> roleRepository.save(new Role(Role.VOTER)));

        Optional<User> existing = userRepository.findByEmail(ADMIN_EMAIL);
        if (existing.isEmpty()) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail(ADMIN_EMAIL);
            admin.setVoterId("ADMIN0001");
            admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(adminRole);
            admin.setStatus(VoterStatus.APPROVED);
            userRepository.save(admin);
            log.info("Default admin created: {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
            return;
        }

        User admin = existing.get();
        boolean changed = false;
        if (resetAdminPassword && !passwordEncoder.matches(ADMIN_PASSWORD, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            changed = true;
        }
        if (admin.isAccountLocked() || admin.getFailedAttempts() != 0) {
            admin.setAccountLocked(false);
            admin.setFailedAttempts(0);
            admin.setLockTime(null);
            changed = true;
        }
        if (admin.getStatus() != VoterStatus.APPROVED || !Role.ADMIN.equals(admin.getRole().getName())) {
            admin.setStatus(VoterStatus.APPROVED);
            admin.setRole(adminRole);
            changed = true;
        }
        if (changed) {
            userRepository.save(admin);
            log.info("Default admin account repaired: {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
        }
    }
}
