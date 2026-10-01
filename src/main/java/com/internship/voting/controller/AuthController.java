package com.internship.voting.controller;

import com.internship.voting.dto.RegistrationDto;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.service.UserService;
import com.internship.voting.util.FormErrors;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Login page and voter self-registration. (The POST /login itself is handled by Spring Security.) */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Sign in");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        return registerView(model, new RegistrationDto(), null);
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationDto form, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return registerView(model, form, result);
        }
        try {
            userService.registerVoter(form);
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return registerView(model, form, result);
        }
        redirectAttributes.addFlashAttribute("success",
                "Registration received. You can sign in now; an admin must approve you before you can vote.");
        return "redirect:/login?registered";
    }

    private String registerView(Model model, RegistrationDto form, BindingResult result) {
        model.addAttribute("form", form);
        model.addAttribute("errors", FormErrors.from(result));
        // The date picker stops at "today minus 18 years" (the server re-checks the age anyway).
        model.addAttribute("maxDob", LocalDate.now().minusYears(18).toString());
        model.addAttribute("pageTitle", "Register to vote");
        return "register";
    }
}
