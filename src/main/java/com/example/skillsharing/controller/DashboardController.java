package com.example.skillsharing.controller;

import com.example.skillsharing.model.User;
import com.example.skillsharing.service.UserService;  // Make sure to inject the UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;  // Inject your UserService

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        // Fetch the full User object using the email or username from userDetails
        User user = userService.findByEmail(userDetails.getUsername());  // Assuming email or username is the identifier
        
        if (user == null) {
            return "redirect:/auth/login";  // Redirect if the user is not found
        }

        model.addAttribute("user", user);  // Add the full User object to the model

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_WORKER"))) {
            return "redirect:/worker/dashboard";
        } else {
            return "redirect:/requester/dashboard";
        }
    }
}
