package com.example.skillsharing.controller;

import com.example.skillsharing.model.Role;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "user/register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user, Model model) {
		boolean hasErrors = false;

		// Validate Name
		if (user.getName() == null || user.getName().trim().isEmpty()) {
			model.addAttribute("nameError", "Name is required.");
			hasErrors = true;
		}

		// Validate Email
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			model.addAttribute("emailError", "Email is required.");
			hasErrors = true;
		} else if (userService.findByEmail(user.getEmail()) != null) {
			model.addAttribute("emailError", "Email is already in use.");
			hasErrors = true;
		}

		// Validate Password
		if (user.getPassword() == null || user.getPassword().length() < 6) {
			model.addAttribute("passwordError", "Password must be at least 6 characters.");
			hasErrors = true;
		}

		// Validate Contact Number
		if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()) {
			model.addAttribute("contactError", "Contact number is required.");
			hasErrors = true;
		} else if (userService.findByContactNumber(user.getContactNumber()) != null) {
			model.addAttribute("contactError", "Contact number is already registered.");
			hasErrors = true;
		}

		// Validate Address
		if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
			model.addAttribute("addressError", "Address is required.");
			hasErrors = true;
		}

		// If there are errors, return to the registration page
		if (hasErrors) {
			model.addAttribute("user", user);
			return "user/register";
		}

		// Set default rating for workers
		if (user.getRole() == Role.WORKER) {
			user.setRating(0.0);
		}

		// Save user
		userService.saveUser(user);
		return "redirect:/auth/login";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		model.addAttribute("user", new User());
		return "user/login";
	}

	@PostMapping("/login")
	public String loginUser(@ModelAttribute User user, Model model) {
		User dbUser = userService.findByEmail(user.getEmail());

		if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			model.addAttribute("error", "Invalid email or password.");
			return "user/login";
		}

		return "redirect:/dashboard"; // Change as per your app's main page
	}
}
