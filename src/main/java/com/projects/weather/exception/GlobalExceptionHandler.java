package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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
