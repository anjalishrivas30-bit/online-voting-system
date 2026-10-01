package com.internship.voting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Central error handling for controllers.
 * NOTE: we deliberately do NOT catch Exception.class, otherwise Spring Security's AccessDeniedException
 * would be swallowed and users would see a 500 page instead of the 403 page.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleBusiness(BusinessException ex) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("status", 400);
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
