package com.example.skillsharing.controller;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.service.SkillListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/requester")
public class RequesterController {

	private final SkillListingService skillListingService;

	// ✅ Constructor-based dependency injection
	public RequesterController(SkillListingService skillListingService) {
		this.skillListingService = skillListingService;
	}

	@GetMapping("/dashboard")
	public String showRequesterDashboard(Model model) {
		List<SkillListing> skills = skillListingService.findRandomWorkersWithSkills();

		// Debugging: Check if profilePicUrl is generated correctly
		for (SkillListing skill : skills) {
			if (skill.getWorker() != null) {
				System.out.println("Worker: " + skill.getWorker().getName() + ", ProfilePicUrl: "
						+ skill.getWorker().getProfilePicUrl());
			}
		}

		model.addAttribute("skills", skills);
		return "requester/dashboard";
	}
}
