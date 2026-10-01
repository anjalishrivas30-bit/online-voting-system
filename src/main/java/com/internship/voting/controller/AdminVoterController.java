package com.internship.voting.controller;

import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.service.UserService;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin: review voter registrations (approve / reject). */
@Controller
@RequestMapping("/admin/voters")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVoterController {

    private final UserService userService;

    public AdminVoterController(UserService userService) {
        this.userService = userService;
    }

    /** Manage voters with simple search (name / email / voter ID) and status filter. */
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       Model model) {
        VoterStatus filter = parseStatus(status);
        model.addAttribute("voters", userService.searchVoters(q, filter));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedStatus", filter == null ? "" : filter.name());
        model.addAttribute("pageTitle", "Manage voters");
        return "admin/voters";
    }

    private VoterStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return VoterStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable("id") Long id,
                          @RequestParam(value = "from", required = false) String from,
                          Principal principal, RedirectAttributes ra) {
        return change(id, VoterStatus.APPROVED, from, principal, ra, "Voter approved.");
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable("id") Long id,
                         @RequestParam(value = "from", required = false) String from,
                         Principal principal, RedirectAttributes ra) {
        return change(id, VoterStatus.REJECTED, from, principal, ra, "Voter rejected.");
    }

    private String change(Long id, VoterStatus status, String from, Principal principal,
                          RedirectAttributes ra, String okMessage) {
        try {
            userService.updateVoterStatus(id, status, principal.getName());
            ra.addFlashAttribute("success", okMessage);
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "dashboard".equals(from) ? "redirect:/admin/dashboard" : "redirect:/admin/voters";
    }
}
