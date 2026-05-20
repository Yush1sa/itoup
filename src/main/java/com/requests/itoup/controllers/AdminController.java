package com.requests.itoup.controllers;

import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;

import com.requests.itoup.services.RequestService;
import com.requests.itoup.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public final UserService userService;

    @GetMapping("/admin/requests")
    public String myrequests(Model model,
                           @AuthenticationPrincipal User currentUser) {
        model.addAttribute("requests", requestService.findAllForUser(currentUser));
        model.addAttribute("employees", userService.findAllEmployees());
        return "admin/requests";
    }


    @PostMapping("/admin/requests/{id}/accept")
    public String acceptRequest(@PathVariable Long id,
                                @AuthenticationPrincipal User currentUser) {

        requestService.updateStatus(id, RequestStatus.ACCEPTED, currentUser);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/start")
    public String startRequest(@PathVariable Long id,
                               @AuthenticationPrincipal User currentUser) {

        requestService.updateStatus(id,
                RequestStatus.IN_PROGRESS, currentUser);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/done")
    public String doneRequest(@PathVariable Long id,
                              @AuthenticationPrincipal User currentUser) {

        requestService.updateStatus(id,
                RequestStatus.DONE, currentUser);

        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, @RequestParam String reason,
                                @AuthenticationPrincipal User currentUser) {

        requestService.rejectRequest(id, reason, currentUser);
        return "redirect:/admin/requests";
    }

    @GetMapping("/admin/requests/{id}")
    public String adminDetails(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal User currentUser) {
        model.addAttribute("request", requestService.findById(id, currentUser));
        model.addAttribute("backUrl", "/admin/requests");
        return "requests/details";
    }

    @PostMapping("/admin/requests/{id}/assign")
    public String assignEmployee(@PathVariable Long id,
                                 @RequestParam Long employeeId,
                                 @AuthenticationPrincipal User currentUser) {
        User employee = userService.findById(employeeId);
        requestService.assignEmployee(id, employee, currentUser);
        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id,
                                @AuthenticationPrincipal User currentUser) {
        requestService.deleteById(id, currentUser);

        return "redirect:/admin/requests";
    }
}
