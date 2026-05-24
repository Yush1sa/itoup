package com.requests.itoup.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @ExceptionHandler(InvalidRequestStateException.class)
    public String handleInvalidRequestState(
            InvalidRequestStateException ex,
            RedirectAttributes redirectAttributes
    ) {

        redirectAttributes.addFlashAttribute(
                "error",
                ex.getMessage()
        );

        return "redirect:/admin/requests";
    }
}