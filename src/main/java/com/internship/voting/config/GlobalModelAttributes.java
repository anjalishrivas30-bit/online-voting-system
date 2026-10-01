package com.internship.voting.config;

import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.Role;
import com.internship.voting.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds data needed by the sidebar/top bar to EVERY page:
 *   navUser, navAdmin, navVoter  - who is logged in and with which role
 *   navPath                      - current URL (to highlight the active menu item)
 *   navPendingRequests           - admin: number of pending voter requests (sidebar badge)
 *   navUnseenRequests            - voter: number of decided requests not yet seen (sidebar badge)
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final RequestService requestService;

    public GlobalModelAttributes(RequestService requestService) {
        this.requestService = requestService;
    }

    @ModelAttribute
    public void addNavigationAttributes(Model model, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        boolean admin = loggedIn && hasRole(auth, Role.ADMIN);
        boolean voter = loggedIn && hasRole(auth, Role.VOTER);

        model.addAttribute("navUser", loggedIn ? auth.getName() : null);
        model.addAttribute("navAdmin", admin);
        model.addAttribute("navVoter", voter);
        model.addAttribute("navPath", request.getRequestURI());
        model.addAttribute("navPendingRequests", admin ? requestService.countByStatus(RequestStatus.PENDING) : 0L);
        model.addAttribute("navUnseenRequests", voter ? requestService.countUnseenFor(auth.getName()) : 0L);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> role.equals(a.getAuthority()));
    }
}
