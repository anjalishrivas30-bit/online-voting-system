package com.internship.voting.controller;

import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.RequestStatus;
import com.internship.voting.service.RequestService;
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

/** Admin: view voter requests, filter by status, open details, approve / reject with a remark. */
@Controller
@RequestMapping("/admin/requests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRequestController {

    private final RequestService requestService;

    public AdminRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public String list(@RequestParam(value = "status", required = false) String status, Model model) {
        RequestStatus filter = parseStatus(status);
        model.addAttribute("requests", requestService.findAll(filter));
        model.addAttribute("selectedStatus", filter == null ? "" : filter.name());
        model.addAttribute("countAll", requestService.countAll());
        model.addAttribute("countPending", requestService.countByStatus(RequestStatus.PENDING));
        model.addAttribute("countApproved", requestService.countByStatus(RequestStatus.APPROVED));
        model.addAttribute("countRejected", requestService.countByStatus(RequestStatus.REJECTED));
        model.addAttribute("pageTitle", "Voter requests");
        return "admin/requests";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("req", requestService.getById(id));
        model.addAttribute("pageTitle", "Request details");
        return "admin/request-detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable("id") Long id,
                          @RequestParam(value = "remark", required = false) String remark,
                          Principal principal, RedirectAttributes ra) {
        return decide(id, RequestStatus.APPROVED, remark, principal, ra, "Request approved. The voter can now see the update.");
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable("id") Long id,
                         @RequestParam(value = "remark", required = false) String remark,
                         Principal principal, RedirectAttributes ra) {
        return decide(id, RequestStatus.REJECTED, remark, principal, ra, "Request rejected. The voter can now see the update.");
    }

    private String decide(Long id, RequestStatus status, String remark, Principal principal,
                          RedirectAttributes ra, String okMessage) {
        try {
            requestService.decide(id, status, remark, principal.getName());
            ra.addFlashAttribute("success", okMessage);
            return "redirect:/admin/requests";
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/requests/" + id;
        }
    }

    private RequestStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return RequestStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
