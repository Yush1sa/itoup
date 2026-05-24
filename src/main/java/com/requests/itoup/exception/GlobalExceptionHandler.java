package com.requests.itoup.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestNotFoundException.class)
    public String handleRequestNotFound(
            RequestNotFoundException ex,
            Model model
    ) {
        model.addAttribute("error", ex.getMessage());

        return "error/error";
    }

    @ExceptionHandler(BusinessAccessDeniedException.class)
    public String handleAccessDenied(
            BusinessAccessDeniedException ex,
            Model model
    ) {
        model.addAttribute("error", ex.getMessage());

        return "error/error";
    }
}