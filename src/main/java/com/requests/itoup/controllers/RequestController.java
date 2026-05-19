package com.requests.itoup.controllers;


import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/requests/create")
    public String createForm(Model model) {

        model.addAttribute("request", new Request());

        return "requests/create";
    }
    @PostMapping("/requests/create")
    public String createRequest(@ModelAttribute Request request,
                                @AuthenticationPrincipal User currentUser){
        requestService.save(request, currentUser);

        return "redirect:/my-requests";
    }

    @GetMapping("/requests/my")
    public String myRequests(Model model,
                             @AuthenticationPrincipal User currentUser){
        model.addAttribute("requests", requestService.findAllForUser(currentUser));

        return "requests/my-requests";
    }

    @GetMapping("/requests/{id}")
    public String userDetails(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal User currentUser) {
        model.addAttribute("request", requestService.findById(id, currentUser));
        model.addAttribute("backUrl", "/my-requests");
        return "requests/details";
    }

    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id, Model model) {
        requestService.deleteById(id);

        return "redirect:/my-requests";
    }
}
