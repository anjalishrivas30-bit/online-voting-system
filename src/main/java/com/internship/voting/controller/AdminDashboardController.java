package com.internship.voting.controller;

import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.VoterRequest;
import com.internship.voting.service.AuditService;
import com.internship.voting.service.CandidateService;
import com.internship.voting.service.ElectionService;
import com.internship.voting.service.RequestService;
import com.internship.voting.service.UserService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final UserService userService;
    private final CandidateService candidateService;
    private final ElectionService electionService;
    private final RequestService requestService;
    private final AuditService auditService;

    public AdminDashboardController(UserService userService, CandidateService candidateService,
                                    ElectionService electionService, RequestService requestService,
                                    AuditService auditService) {
        this.userService = userService;
        this.candidateService = candidateService;
        this.electionService = electionService;
        this.requestService = requestService;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // The five summary cards
        model.addAttribute("totalVoters", userService.countVoters());
        model.addAttribute("totalCandidates", candidateService.count());
        model.addAttribute("totalElections", electionService.count());
        model.addAttribute("activeElections", electionService.countActive());
        long pendingRequests = requestService.countByStatus(RequestStatus.PENDING);
        model.addAttribute("pendingRequests", pendingRequests);

        // Latest pending requests (max 5) and voter registrations waiting for approval
        List<VoterRequest> pending = requestService.findAll(RequestStatus.PENDING);
        model.addAttribute("pendingRequestList", pending.size() > 5 ? pending.subList(0, 5) : pending);
        model.addAttribute("pendingVoters", userService.findPendingVoters());
        model.addAttribute("pageTitle", "Admin dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        model.addAttribute("logs", auditService.latest());
        model.addAttribute("pageTitle", "Audit log");
        return "admin/audit";
    }
}
