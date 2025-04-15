package com.example.skillsharing.controller;

import com.example.skillsharing.model.Role;
import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;
import com.example.skillsharing.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WorkerController {

	private final WorkerService workerService;
	private final SkillListingService skillListingService;
	private final UserService userService;

	public WorkerController(WorkerService workerService, SkillListingService skillListingService,
			UserService userService) {
		this.workerService = workerService;
		this.skillListingService = skillListingService;
		this.userService = userService;
	}

	@GetMapping("/workers/search")
	public String searchWorkers(@RequestParam("skill") String searchTerm, Model model) {
		List<SkillListing> skills = skillListingService.searchBySkillOrCategory(searchTerm);
		model.addAttribute("skills", skills);
		return "search-results";
	}

	@GetMapping("/worker/dashboard")
	public String workerDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/auth/login"; // Redirect if not logged in
		}

		// ✅ Get logged-in user
		User worker = userService.findByEmail(userDetails.getUsername());

		// ✅ Prevent null worker errors
		if (worker == null || worker.getRole() != Role.WORKER) {
			return "error"; // Ensure only workers can access
		}

		// ✅ Fetch skills for the worker
		List<SkillListing> skills = skillListingService.getSkillsByWorker(worker.getId());

		// ✅ Add attributes to the model
		model.addAttribute("skills", skills);
		model.addAttribute("worker", worker);

		return "worker/dashboard"; // ✅ Display worker dashboard
	}
}
