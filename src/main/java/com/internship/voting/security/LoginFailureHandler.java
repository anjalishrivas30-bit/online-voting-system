package com.internship.voting.security;

import com.internship.voting.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * After a failed login: count the failure (lock at 5), audit it and show the right message.
 * The login form field is called "email" (see SecurityConfig.usernameParameter).
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    public LoginFailureHandler(LoginAttemptService loginAttemptService, AuditService auditService) {
        this.loginAttemptService = loginAttemptService;
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String email = request.getParameter("email");
        String base = request.getContextPath() + "/login";
        if (email == null || email.isBlank()) {
            response.sendRedirect(base + "?error");
            return;
        }

        if (exception instanceof LockedException) {
            // Account is already locked: Spring refused the login before even checking the password.
            auditService.log(email, "LOGIN_BLOCKED", "Login attempt on a locked account");
            response.sendRedirect(base + "?locked");
            return;
        }

        boolean lockedNow = false;
        if (exception instanceof BadCredentialsException) {
            lockedNow = loginAttemptService.recordFailure(email);
        }
        auditService.log(email, lockedNow ? "ACCOUNT_LOCKED" : "LOGIN_FAILED",
                lockedNow ? "Account locked after " + LoginAttemptService.MAX_ATTEMPTS + " failed attempts"
                          : "Wrong email or password");
        response.sendRedirect(base + (lockedNow ? "?locked" : "?error"));
    }
}
