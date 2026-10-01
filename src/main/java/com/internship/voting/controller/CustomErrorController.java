package com.internship.voting.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/** Custom 403 / 404 / generic error pages. No stack traces are ever shown to users. */
@Controller
public class CustomErrorController implements ErrorController {

    /** Container errors (unknown URL -> 404, unexpected exception -> 500). */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Object statusAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = 500;
        if (statusAttr != null) {
            try {
                status = Integer.parseInt(statusAttr.toString());
            } catch (NumberFormatException ignored) {
                status = 500;
            }
        }
        response.setStatus(status);
        if (status == 404) {
            return "error/404";
        }
        if (status == 403) {
            return "error/403";
        }
        model.addAttribute("status", status);
        model.addAttribute("message", "Something went wrong. Please try again.");
        return "error/generic";
    }

    /**
     * Target of Spring Security's accessDeniedPage. It is reached by a FORWARD that keeps the original HTTP
     * method (could be POST), so this mapping accepts every method.
     */
    @RequestMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
