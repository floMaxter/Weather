package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public String handleInvalidPasswordException(InvalidPasswordException ex,
                                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/auth/login";
    }

    @ExceptionHandler(LocationAlreadyExistsException.class)
    public String handleLocationAlreadyExistsException(LocationAlreadyExistsException ex,
                                                       RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/weather";
    }

    @ExceptionHandler(GenericApplicationException.class)
    public String handleGenericApplicationException(GenericApplicationException ex,
                                                    HttpServletResponse response,
                                                    Model model) {
        response.setStatus(ex.getStatusCode());
        model.addAttribute("error", ex.getMessage());
        return "error/error_page";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnhandledException(HttpServletResponse response,
                                  Model model) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", "Something went wrong.");
        return "error/error_page";
    }
}
