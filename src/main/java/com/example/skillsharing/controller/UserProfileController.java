package com.example.skillsharing.controller;

import com.example.skillsharing.model.User;

import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Controller
public class UserProfileController {

	private final UserService userService;
	private final FeedbackService feedbackService;

	public UserProfileController(UserService userService, FeedbackService feedbackService) {
		this.userService = userService;
		this.feedbackService = feedbackService;
	}

	@GetMapping("/profile")
	public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/auth/login"; // Redirect if not logged in
		}

		User user = userService.findByEmail(userDetails.getUsername());

		if (user == null) {
			return "error";
		}

		double averageRating = feedbackService.getAverageRatingForUser(user.getId());

		model.addAttribute("user", user);
		model.addAttribute("averageRating", averageRating);

		return "user/profile"; // ✅ Show profile page with rating
	}

	// 🟢 Show Update Profile Page
	@GetMapping("/profile/update")
	public String showUpdateProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails == null) {
			return "redirect:/auth/login";
		}

		User user = userService.findByEmail(userDetails.getUsername());

		if (user == null) {
			return "error";
		}

		model.addAttribute("user", user);
		return "user/update-profile"; // ✅ Show update form
	}

	// 🟢 Handle Profile Update
	@PostMapping("/profile/update")
	public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute User updatedUser,
			@RequestParam("profileImage") MultipartFile profileImage) {

		if (userDetails == null) {
			return "redirect:/auth/login";
		}

		User user = userService.findByEmail(userDetails.getUsername());

		if (user != null) {
			user.setName(updatedUser.getName());
			user.setContactNumber(updatedUser.getContactNumber());
			user.setAddress(updatedUser.getAddress());

			// Store the image as a BLOB in the database
			if (!profileImage.isEmpty()) {
				try {
					user.setProfilePicture(profileImage.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			userService.updateUser(user);
		}

		return "redirect:/profile?success";
	}

	@GetMapping("/profile/image")
	@ResponseBody
	public ResponseEntity<byte[]> getProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.findByEmail(userDetails.getUsername());

		if (user == null || user.getProfilePicture() == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "image/jpeg");

		return new ResponseEntity<>(user.getProfilePicture(), headers, HttpStatus.OK);
	}
}
