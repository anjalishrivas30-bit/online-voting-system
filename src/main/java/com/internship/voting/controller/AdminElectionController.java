package com.internship.voting.controller;

import com.internship.voting.dto.ElectionForm;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import com.internship.voting.service.ElectionService;
import com.internship.voting.service.ResultService;
import com.internship.voting.util.FormErrors;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.temporal.ChronoUnit;
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

/** Admin: create / edit / delete / close elections and view live results. */
@Controller
@RequestMapping("/admin/elections")
@PreAuthorize("hasRole('ADMIN')")
public class AdminElectionController {

    private final ElectionService electionService;
    private final ResultService resultService;

    public AdminElectionController(ElectionService electionService, ResultService resultService) {
        this.electionService = electionService;
        this.resultService = resultService;
    }

    /** Filter: UPCOMING / ACTIVE / COMPLETED (completed = CLOSED in the database). */
    @GetMapping
    public String list(@RequestParam(value = "status", required = false) String status, Model model) {
        ElectionStatus filter = parseStatus(status);
        model.addAttribute("elections", electionService.findByStatus(filter));
        model.addAttribute("selectedStatus", filter == null ? "" : filter.name());
        model.addAttribute("pageTitle", "Manage elections");
        return "admin/elections";
    }

    private ElectionStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String value = status.trim().toUpperCase();
        if ("COMPLETED".equals(value)) {
            return ElectionStatus.CLOSED;
        }
        try {
            return ElectionStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return formView(model, new ElectionForm(), null, "/admin/elections", "Create election");
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ElectionForm form, BindingResult result,
                         Principal principal, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formView(model, form, result, "/admin/elections", "Create election");
        }
        try {
            electionService.create(form, principal.getName());
            ra.addFlashAttribute("success", "Election created. Now add candidates to it.");
            return "redirect:/admin/elections";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return formView(model, form, result, "/admin/elections", "Create election");
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Election election = electionService.getById(id);
        ElectionForm form = new ElectionForm();
        form.setTitle(election.getTitle());
        form.setDescription(election.getDescription());
        form.setStartTime(election.getStartTime().truncatedTo(ChronoUnit.MINUTES));
        form.setEndTime(election.getEndTime().truncatedTo(ChronoUnit.MINUTES));
        return formView(model, form, null, "/admin/elections/" + id + "/edit", "Edit election");
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id, @Valid @ModelAttribute("form") ElectionForm form,
                         BindingResult result, Principal principal, Model model, RedirectAttributes ra) {
        String action = "/admin/elections/" + id + "/edit";
        if (result.hasErrors()) {
            return formView(model, form, result, action, "Edit election");
        }
        try {
            electionService.update(id, form, principal.getName());
            ra.addFlashAttribute("success", "Election updated.");
            return "redirect:/admin/elections";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return formView(model, form, result, action, "Edit election");
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
        try {
            electionService.delete(id, principal.getName());
            ra.addFlashAttribute("success", "Election deleted.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
        try {
            electionService.closeNow(id, principal.getName());
            ra.addFlashAttribute("success", "Election closed. Results are now public.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    /** Admins may see results while the election is still running (adminView = true). */
    @GetMapping("/{id}/results")
    public String results(@PathVariable("id") Long id, Model model) {
        model.addAttribute("result", resultService.getResults(id, true));
        model.addAttribute("pageTitle", "Election results");
        return "results";
    }

    private String formView(Model model, ElectionForm form, BindingResult result, String action, String heading) {
        model.addAttribute("form", form);
        model.addAttribute("errors", FormErrors.from(result));
        model.addAttribute("formAction", action);
        model.addAttribute("heading", heading);
        model.addAttribute("pageTitle", heading);
        return "admin/election-form";
    }
}
