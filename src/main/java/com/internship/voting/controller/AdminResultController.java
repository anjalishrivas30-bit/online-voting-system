package com.internship.voting.controller;

import com.internship.voting.service.ElectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Admin "Results" menu: list of all elections with a link to each election's result page. */
@Controller
@RequestMapping("/admin/results")
@PreAuthorize("hasRole('ADMIN')")
public class AdminResultController {

    private final ElectionService electionService;

    public AdminResultController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("elections", electionService.findAll());
        model.addAttribute("pageTitle", "Results");
        return "admin/results";
    }
}
