package com.internship.voting.controller;

import com.internship.voting.dto.RequestForm;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.RequestType;
import com.internship.voting.service.RequestService;
import com.internship.voting.util.FormErrors;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Voter: submit a request and track its status. A voter only ever sees his or her own requests. */
@Controller
@RequestMapping("/voter/requests")
@PreAuthorize("hasRole('VOTER')")
public class VoterRequestController {

    private final RequestService requestService;

    public VoterRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public String myRequests(Principal principal, Model model) {
        model.addAttribute("requests", requestService.findForVoter(principal.getName()));
        model.addAttribute("pageTitle", "My requests");
        // Opening this page counts as "seen": clears the sidebar badge / dashboard notification.
        requestService.markSeen(principal.getName());
        return "voter/requests";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return formView(model, new RequestForm(), null);
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") RequestForm form, BindingResult result,
                         Principal principal, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formView(model, form, result);
        }
        try {
            requestService.submit(principal.getName(), form.getType(), form.getDescription());
            ra.addFlashAttribute("success", "Request submitted. An admin will review it soon.");
            return "redirect:/voter/requests";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return formView(model, form, result);
        }
    }

    private String formView(Model model, RequestForm form, BindingResult result) {
        model.addAttribute("form", form);
        model.addAttribute("errors", FormErrors.from(result));
        model.addAttribute("requestTypes", RequestType.values());
        model.addAttribute("pageTitle", "Submit request");
        return "voter/request-form";
    }
}
