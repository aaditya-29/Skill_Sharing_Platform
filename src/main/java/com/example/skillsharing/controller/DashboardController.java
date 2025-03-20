package com.example.skillsharing.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal UserDetails userDetails) {
		System.out.println("📌 Inside dashboard() method");

		if (userDetails == null) {
			System.out.println("❌ UserDetails is null! User is not logged in.");
			return "redirect:/auth/login";
		}

		System.out.println("✅ Logged-in User: " + userDetails.getUsername());
		System.out.println("🔍 Roles: " + userDetails.getAuthorities());

		if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_WORKER"))) {
			System.out.println("➡ Redirecting to worker dashboard...");
			return "redirect:/worker/dashboard";
		} else {
			System.out.println("➡ Redirecting to requester dashboard...");
			return "redirect:/requester/dashboard";
		}
	}

}
