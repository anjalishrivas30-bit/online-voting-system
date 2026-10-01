package com.internship.voting.controller;

import com.internship.voting.dto.CandidateForm;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.Candidate;
import com.internship.voting.service.CandidateService;
import com.internship.voting.service.ElectionService;
import com.internship.voting.util.FormErrors;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin: add / edit / delete candidates and assign them to an election. */
@Controller
@RequestMapping("/admin/candidates")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCandidateController {

    private final CandidateService candidateService;
    private final ElectionService electionService;

    public AdminCandidateController(CandidateService candidateService, ElectionService electionService) {
        this.candidateService = candidateService;
        this.electionService = electionService;
    }

    @GetMapping
    public String list(@RequestParam(value = "electionId", required = false) Long electionId,
                       @RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("candidates", candidateService.search(electionId, q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("elections", electionService.findAll());
        model.addAttribute("selectedElectionId", electionId);
        model.addAttribute("pageTitle", "Manage candidates");
        return "admin/candidates";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(value = "electionId", required = false) Long electionId, Model model) {
        CandidateForm form = new CandidateForm();
        form.setElectionId(electionId);
        return formView(model, form, null, "/admin/candidates", "Add candidate", false);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CandidateForm form, BindingResult result,
                         Principal principal, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formView(model, form, result, "/admin/candidates", "Add candidate", false);
        }
        try {
            candidateService.create(form, principal.getName());
            ra.addFlashAttribute("success", "Candidate added.");
            return "redirect:/admin/candidates?electionId=" + form.getElectionId();
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return formView(model, form, result, "/admin/candidates", "Add candidate", false);
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Candidate c = candidateService.getById(id);
        CandidateForm form = new CandidateForm();
        form.setName(c.getName());
        form.setParty(c.getParty());
        form.setPosition(c.getPosition());
        form.setDescription(c.getDescription());
        form.setElectionId(c.getElection().getId());
        return formView(model, form, null, "/admin/candidates/" + id + "/edit", "Edit candidate", true);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute("form") CandidateForm form,
                         BindingResult result, Principal principal, Model model, RedirectAttributes ra) {
        String action = "/admin/candidates/" + id + "/edit";
        if (result.hasErrors()) {
            return formView(model, form, result, action, "Edit candidate", true);
        }
        try {
            Candidate saved = candidateService.update(id, form, principal.getName());
            ra.addFlashAttribute("success", "Candidate updated.");
            return "redirect:/admin/candidates?electionId=" + saved.getElection().getId();
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return formView(model, form, result, action, "Edit candidate", true);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
        try {
            candidateService.delete(id, principal.getName());
            ra.addFlashAttribute("success", "Candidate deleted.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/candidates";
    }

    private String formView(Model model, CandidateForm form, BindingResult result, String action,
                            String heading, boolean editing) {
        model.addAttribute("form", form);
        model.addAttribute("errors", FormErrors.from(result));
        model.addAttribute("elections", electionService.findAll());
        model.addAttribute("formAction", action);
        model.addAttribute("heading", heading);
        model.addAttribute("editing", editing);
        model.addAttribute("pageTitle", heading);
        return "admin/candidate-form";
    }
}
