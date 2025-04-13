package com.example.skillsharing.controller;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.SkillListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/requester")
public class RequesterController {

	private final SkillListingService skillListingService;
	private final FeedbackService feedbackService;

	// ✅ Constructor-based dependency injection
	public RequesterController(SkillListingService skillListingService, FeedbackService feedbackService) {
		this.skillListingService = skillListingService;
		this.feedbackService = feedbackService;
	}

	@GetMapping("/dashboard")
	public String showRequesterDashboard(Model model) {
		List<SkillListing> skills = skillListingService.findRandomWorkersWithSkills();
		
		Map<Long, Double> workerRatings = new HashMap<>();


		// Debugging: Check if profilePicUrl is generated correctly
		for (SkillListing skill : skills) {
			if (skill.getWorker() != null) {
				Long workerId = skill.getWorker().getId();
				Double rating = feedbackService.getAverageRatingForUser(workerId);
				workerRatings.put(workerId, rating != null ? rating : 0.0);
			}
		}

		model.addAttribute("skills", skills);
		model.addAttribute("workerRatings", workerRatings);

		return "requester/dashboard";
	}
}
