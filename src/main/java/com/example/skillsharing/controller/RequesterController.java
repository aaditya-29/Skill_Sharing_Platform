package com.example.skillsharing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/requester")
public class RequesterController {

	@GetMapping("/dashboard")
	public String showRequesterDashboard(Model model) {
		model.addAttribute("message", "Welcome to the Requester Dashboard!");
		return "requester/dashboard";
	}

}
