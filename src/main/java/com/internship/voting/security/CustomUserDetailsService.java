package com.internship.voting.security;

import com.internship.voting.model.User;
import com.internship.voting.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tells Spring Security HOW to load a user at login time: by email, from our MySQL users table.
 * Spring Security then compares the submitted password with the stored BCrypt hash.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lockout lasts LOCK_MINUTES, after that the account unlocks itself on the next login attempt.
        if (user.isAccountLocked() && user.getLockTime() != null
                && user.getLockTime().plusMinutes(LoginAttemptService.LOCK_MINUTES).isBefore(LocalDateTime.now())) {
            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,                       // enabled
                true,                       // account not expired
                true,                       // credentials not expired
                !user.isAccountLocked(),    // account not locked
                List.of(new SimpleGrantedAuthority(user.getRole().getName())));
    }
}
