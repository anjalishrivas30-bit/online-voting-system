package com.internship.voting.security;

import com.internship.voting.model.User;
import com.internship.voting.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Brute-force protection: lock the account after 5 wrong passwords in a row. */
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;

    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** @return true if this failure caused the account to become locked. */
    @Transactional
    public boolean recordFailure(String email) {
        Optional<User> found = userRepository.findByEmail(email.trim().toLowerCase());
        if (found.isEmpty()) {
            return false; // unknown email: nothing to lock (and we do not reveal that it is unknown)
        }
        User user = found.get();
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        boolean nowLocked = false;
        if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
            nowLocked = true;
        }
        userRepository.save(user);
        return nowLocked;
    }

    @Transactional
    public void reset(String email) {
        userRepository.findByEmail(email.trim().toLowerCase()).ifPresent(user -> {
            if (user.getFailedAttempts() != 0 || user.isAccountLocked()) {
                user.setFailedAttempts(0);
                user.setAccountLocked(false);
                user.setLockTime(null);
                userRepository.save(user);
            }
        });
    }
}
