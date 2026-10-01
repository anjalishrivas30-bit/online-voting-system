package com.internship.voting.security;

import com.internship.voting.model.Role;
import com.internship.voting.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/** After a successful login: reset failed attempts, write an audit entry, redirect by role. */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    public LoginSuccessHandler(LoginAttemptService loginAttemptService, AuditService auditService) {
        this.loginAttemptService = loginAttemptService;
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        loginAttemptService.reset(email);
        auditService.log(email, "LOGIN_SUCCESS", "User logged in");

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> Role.ADMIN.equals(a.getAuthority()));
        response.sendRedirect(request.getContextPath() + (admin ? "/admin/dashboard" : "/voter/home"));
    }
}
