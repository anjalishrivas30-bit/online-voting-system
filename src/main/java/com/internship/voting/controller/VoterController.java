package com.internship.voting.controller;

import com.internship.voting.dto.VoterElectionView;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.Candidate;
import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.User;
import com.internship.voting.model.VoterRequest;
import com.internship.voting.service.CandidateService;
import com.internship.voting.service.ElectionService;
import com.internship.voting.service.RequestService;
import com.internship.voting.service.ResultService;
import com.internship.voting.service.UserService;
import com.internship.voting.service.VoteService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Voter area: dashboard, profile, election list with eligibility, ballot, confirmation, cast vote, results.
 * Everything under /voter/** is protected by SecurityConfig (ROLE_VOTER) and by @PreAuthorize below.
 */
@Controller
@RequestMapping("/voter")
@PreAuthorize("hasRole('VOTER')")
public class VoterController {

    private final UserService userService;
    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoteService voteService;
    private final ResultService resultService;
    private final RequestService requestService;

    public VoterController(UserService userService, ElectionService electionService,
                           CandidateService candidateService, VoteService voteService,
                           ResultService resultService, RequestService requestService) {
        this.userService = userService;
        this.electionService = electionService;
        this.candidateService = candidateService;
        this.voteService = voteService;
        this.resultService = resultService;
        this.requestService = requestService;
    }

    /** Voter dashboard: profile, active/upcoming elections, my requests, voting status, quick actions. */
    @GetMapping({"/home", "/dashboard"})
    public String home(Principal principal, Model model) {
        User voter = userService.getByEmail(principal.getName());

        List<VoterElectionView> active = new ArrayList<>();
        List<VoterElectionView> upcoming = new ArrayList<>();
        Long quickVoteElectionId = null;
        for (Election election : electionService.findAll()) {
            VoterElectionView view = voteService.describe(voter, election);
            ElectionStatus status = election.getEffectiveStatus();   // live status computed from the clock
            if (status == ElectionStatus.ACTIVE) {
                active.add(view);
                if (quickVoteElectionId == null && view.isCanVote()) {
                    quickVoteElectionId = election.getId();
                }
            } else if (status == ElectionStatus.UPCOMING) {
                upcoming.add(view);
            }
        }

        List<VoterRequest> requests = requestService.findForVoter(principal.getName());
        long pendingRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();

        // In-app notifications: decided requests the voter has not seen yet. Shown once, then marked as seen.
        List<VoterRequest> notifications = requestService.unseenDecisions(principal.getName());
        requestService.markSeen(principal.getName());

        model.addAttribute("voter", voter);
        model.addAttribute("activeElections", active);
        model.addAttribute("upcomingElections", upcoming);
        model.addAttribute("quickVoteElectionId", quickVoteElectionId);
        model.addAttribute("electionsVoted", voteService.countElectionsVoted(voter.getId()));
        model.addAttribute("recentRequests", requests.size() > 5 ? requests.subList(0, 5) : requests);
        model.addAttribute("pendingRequestCount", pendingRequests);
        model.addAttribute("notifications", notifications);
        model.addAttribute("pageTitle", "Voter dashboard");
        return "voter/dashboard";
    }

    /** All elections with the voter's eligibility message for each. */
    @GetMapping("/elections")
    public String elections(Principal principal, Model model) {
        User voter = userService.getByEmail(principal.getName());
        List<VoterElectionView> views = new ArrayList<>();
        for (Election election : electionService.findAll()) {
            views.add(voteService.describe(voter, election));
        }
        model.addAttribute("elections", views);
        model.addAttribute("pageTitle", "Elections");
        return "voter/elections";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("voter", userService.getByEmail(principal.getName()));
        model.addAttribute("pageTitle", "My profile");
        return "voter/profile";
    }

    /** Step 1: show the ballot (only if the voter is allowed to vote right now). */
    @GetMapping("/elections/{id}/vote")
    public String ballot(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes ra) {
        try {
            Election election = voteService.assertEligible(principal.getName(), id);
            model.addAttribute("election", election);
            model.addAttribute("candidates", candidateService.findByElection(id));
            model.addAttribute("pageTitle", "Cast your vote");
            return "voter/vote";
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/voter/elections";
        }
    }

    /** Step 2: confirmation page - the vote is NOT saved yet. */
    @PostMapping("/elections/{id}/confirm")
    public String confirm(@PathVariable("id") Long id,
                          @RequestParam(value = "candidateId", required = false) Long candidateId,
                          Principal principal, Model model, RedirectAttributes ra) {
        if (candidateId == null) {
            ra.addFlashAttribute("error", "Please select a candidate before continuing.");
            return "redirect:/voter/elections/" + id + "/vote";
        }
        try {
            Candidate candidate = voteService.previewVote(principal.getName(), id, candidateId);
            model.addAttribute("election", candidate.getElection());
            model.addAttribute("candidate", candidate);
            model.addAttribute("pageTitle", "Confirm your vote");
            return "voter/confirm";
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/voter/elections";
        }
    }

    /** Step 3: the vote is saved (one transaction). A second attempt is refused by VoteService. */
    @PostMapping("/elections/{id}/cast")
    public String cast(@PathVariable("id") Long id,
                       @RequestParam(value = "candidateId", required = false) Long candidateId,
                       Principal principal, RedirectAttributes ra) {
        try {
            voteService.castVote(principal.getName(), id, candidateId);
            ra.addFlashAttribute("success", "Vote successfully submitted. Thank you for voting!");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/voter/elections";
    }

    /** Results are only available after the election is completed (ResultService enforces this). */
    @GetMapping("/elections/{id}/results")
    public String results(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("result", resultService.getResults(id, false));
            model.addAttribute("pageTitle", "Election results");
            return "results";
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/voter/elections";
        }
    }
}
