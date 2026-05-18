package com.requests.itoup.controllers;

import com.requests.itoup.models.enums.RequestStatus;

import com.requests.itoup.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final RequestService requestService;

    @GetMapping("/admin/requests")
    public String requests(Model model){
        model.addAttribute("requests", requestService.findAll());

        return "admin/requests";
    }


    @PostMapping("/admin/requests/{id}/accept")
    public String acceptRequest(@PathVariable Long id) {

        requestService.updateStatus(id, RequestStatus.ACCEPTED);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/start")
    public String startRequest(@PathVariable Long id) {

        requestService.updateStatus(id,
                RequestStatus.IN_PROGRESS);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/done")
    public String doneRequest(@PathVariable Long id) {

        requestService.updateStatus(id,
                RequestStatus.DONE);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, @RequestParam String reason) {

        requestService.rejectRequest(id, reason);
        return "redirect:/admin/requests";
    }

    @GetMapping("/admin/requests/{id}")
    public String adminDetails(@PathVariable Long id, Model model) {
        model.addAttribute("request", requestService.findById(id));
        model.addAttribute("backUrl", "/admin/requests");
        return "requests/details";
    }
}
