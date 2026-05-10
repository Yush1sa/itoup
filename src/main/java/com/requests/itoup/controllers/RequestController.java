package com.requests.itoup.controllers;


import com.requests.itoup.models.Request;
import com.requests.itoup.services.RequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/requests/create")
    public String createForm(Model model) {

        model.addAttribute("request", new Request());

        return "requests/create";
    }
    @PostMapping("/requests/create")
    public String createRequest(@ModelAttribute Request request){
        requestService.save(request);

        return "redirect:/my-requests";
    }

    @GetMapping("/my-requests")
    public String myRequests(Model model){
        model.addAttribute("requests", requestService.findAll());

        return "requests/my-requests";
    }

    @GetMapping("/requests/{id}")
    public String requestById(@PathVariable Long id, Model model){
        Request request = requestService.findById(id);

        model.addAttribute("request", request);

        return "requests/details";
    }

    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id, Model model) {
        requestService.deleteById(id);

        return "redirect:/my-requests";
    }
}
