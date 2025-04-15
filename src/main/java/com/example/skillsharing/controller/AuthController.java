package com.example.skillsharing.controller;

import com.example.skillsharing.model.Role;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/auth/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "user/register";
	}

	@PostMapping("/auth/register")
	public String registerUser(@ModelAttribute User user, Model model) throws MessagingException {
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
		// Send welcome email to the newly registered user
		String subject = "Welcome to Taskoria – Your Account Has Been Created!";
		String body = "<h3>Hello " + user.getName() + ",</h3>"
				+ "<p>Welcome to <strong>Taskoria</strong>! Your account has been successfully created and you're all set to get started.</p>"
				+ "<p>Whether you're here to hire skilled professionals or offer your expertise, Taskoria is designed to make the process smooth and efficient.</p>"
				+ "<p>If you did not register this account, please contact our support team immediately.</p>" + "<br>"
				+ "<p>We're excited to have you on board!</p>" + "<p>Regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(user.getEmail(), subject, body);

		return "redirect:/auth/login";
	}

	@GetMapping("/auth/login")
	public String showLoginForm(Model model) {
		model.addAttribute("user", new User());
		return "user/login";
	}

	@PostMapping("/auth/login")
	public String loginUser(@ModelAttribute User user, Model model) {
		User dbUser = userService.findByEmail(user.getEmail());

		if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			model.addAttribute("error", "Invalid email or password.");
			return "user/login";
		}

		return "redirect:/dashboard"; // Change as per your app's main page
	}

	@GetMapping("/auth/logout")
	public String logoutUser(HttpSession session) {
		// Invalidate the session
		session.invalidate();

		// Clear authentication context
		SecurityContextHolder.clearContext();

		return "redirect:/auth/login"; // Redirect to login page after logout
	}

	@GetMapping("/updatePassword")
	public String showUpdatePasswordForm() {
		return "user/updatePassword";
	}

	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
			@RequestParam String confirmPassword, Model model, Principal principal) throws MessagingException {
		User currentUser = userService.findByEmail(principal.getName());

		if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
			model.addAttribute("error", "Current password is incorrect.");
			return "user/updatePassword";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "New password and confirmation do not match.");
			return "user/updatePassword";
		}

		currentUser.setPassword(passwordEncoder.encode(newPassword));
		userService.saveUser(currentUser);

//		email notificaiton
		String subject = "Your Taskoria Password Has Been Successfully Updated";

		String body = "<h3>Hello " + currentUser.getName() + ",</h3>"
				+ "<p>We wanted to inform you that the password for your <strong>Taskoria</strong> account was successfully updated.</p>"
				+ "<p>If you initiated this change, no further action is needed.</p>"
				+ "<p><strong>Didn't update your password?</strong> Please contact our support team immediately to secure your account.</p>"
				+ "<br>" + "<p>Thank you for trusting Taskoria.</p>"
				+ "<p>Warm regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(currentUser.getEmail(), subject, body);

		model.addAttribute("success", "Password updated successfully.");
		return "redirect:/profile";

	}
}
