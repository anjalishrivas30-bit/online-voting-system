package com.internship.voting.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validates the role selected on the login page.
 *
 * VOTER selection requires ROLE_VOTER.
 * ADMIN selection requires ROLE_ADMIN.
 */
@Component
public class RoleAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public RoleAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if ("/login".equals(request.getServletPath())
                && "POST".equalsIgnoreCase(request.getMethod())) {

            String selectedRole = request.getParameter("loginRole");
            String email = request.getParameter("email");

            if (selectedRole != null && email != null && !email.trim().isEmpty()) {

                User user = userRepository
                        .findByEmail(email.trim().toLowerCase())
                        .orElse(null);

                if (user != null && user.getRole() != null) {

                    String actualRole = user.getRole().getName();

                    boolean validRole =
                            ("ADMIN".equalsIgnoreCase(selectedRole)
                                    && Role.ADMIN.equals(actualRole))
                            ||
                            ("VOTER".equalsIgnoreCase(selectedRole)
                                    && Role.VOTER.equals(actualRole));

                    if (!validRole) {
                        response.sendRedirect(
                                request.getContextPath()
                                        + "/login?roleError=true");
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}