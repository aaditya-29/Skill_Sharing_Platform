package com.example.skillsharing.controller;

import com.example.skillsharing.model.User;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserProfileControlle {

	@Autowired
	private UserService userService;

	@Autowired
	private FeedbackService feedbackService;

	@GetMapping("/profile/{userId}")
	public String viewProfile(@PathVariable Long userId, Model model) {
		User user = userService.getUserById(userId);
		model.addAttribute("user", user);
		model.addAttribute("feedbacks", feedbackService.getFeedbackForUser(userId));
		return "profile/view-profile";
	}
}
