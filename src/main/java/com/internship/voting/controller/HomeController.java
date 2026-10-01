package com.internship.voting.controller;

import com.internship.voting.model.Role;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** "/" sends everybody to the right place: login page, admin dashboard or voter home. */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> Role.ADMIN.equals(a.getAuthority()));
        return admin ? "redirect:/admin/dashboard" : "redirect:/voter/home";
    }
}
