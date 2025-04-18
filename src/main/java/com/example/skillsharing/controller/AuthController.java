package com.example.skillsharing.controller;

import com.example.skillsharing.model.Role;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		System.out.println("Before  before saving, enabled = " + user.isEnabled()); // 🔥 Must say false

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

		if (hasErrors) {
			model.addAttribute("user", user);
			return "user/register";
		}

		if (user.getRole() == Role.WORKER) {
			user.setRating(0.0);
		}

		String otp = String.format("%06d", new Random().nextInt(999999));
		user.setOtp(otp);
		user.setOtpGeneratedTime(LocalDateTime.now());

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(false);
		System.out.println("Before saving, enabled = " + user.isEnabled()); // 🔥 Must say false
		userService.saveUser(user);

		// Send OTP email
		String subject = "Verify Your Email - Taskoria";
		String body = "<h3>Hello " + user.getName() + ",</h3>"
				+ "<p>Your OTP for verifying your Taskoria account is: <strong>" + otp + "</strong></p>"
				+ "<p>This OTP is valid for 5 minutes.</p>" + "<p>Regards,<br><strong>Team Taskoria</strong></p>";
		emailService.sendEmail(user.getEmail(), subject, body);

		return "redirect:/auth/verify-otp?email=" + user.getEmail();
	}

	@GetMapping("/auth/login")
	public String showLoginForm(Model model) {
		model.addAttribute("user", new User());
		return "user/login";
	}

//	@PostMapping("/auth/login")
//	public String loginUser(@ModelAttribute User user, Model model) {
//		try {
//			User dbUser = userService.findByEmail(user.getEmail());
//			if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
//				model.addAttribute("error", "Invalid email or password.");
//				System.out.println("**********Test invalid passs***********");
//				return "user/login";
//			}
//			if (!dbUser.isEnabled()) {
//				model.addAttribute("error", "Email not verified. Please check your inbox.");
//				System.out.println("**********Test invalid enabled***********");
//
//				return "user/login";
//			}
//		} catch (DisabledException e) {
//			model.addAttribute("error", e.getMessage());
//			return "user/login";
//		}
//		
//		System.out.println("**********Test  redirecttt***********");
//
//		
//		return "redirect:/dashboard";
//	}

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

	@GetMapping("/auth/verify-otp")
	public String showOtpForm(@RequestParam String email, Model model) {
		model.addAttribute("email", email);
		return "user/verify_otp";
	}

	@PostMapping("/auth/verify-otp")
	public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
		User user = userService.findByEmail(email);
		if (user == null) {
			model.addAttribute("error", "User not found.");
			return "user/verify_otp";
		}

		if (user.getOtp().equals(otp) && user.getOtpGeneratedTime().isAfter(LocalDateTime.now().minusMinutes(5))) {
			user.setEnabled(true);
			System.out.println("Testttttttttttttt");
			user.setOtp(null);
			user.setOtpGeneratedTime(null);
			userService.saveUser(user);
			return "redirect:/auth/login?verified";
		} else {
			model.addAttribute("error", "Invalid or expired OTP.");
			model.addAttribute("email", email);
			return "user/verify_otp";
		}
	}

	@PostMapping("/auth/resend-otp")
	public String resendOtp(@RequestParam String email, RedirectAttributes redirectAttributes)
			throws MessagingException {
		User user = userService.findByEmail(email);

		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "User not found.");
			return "redirect:/auth/verify-otp?email=" + email;
		}

		String otp = String.format("%06d", new Random().nextInt(999999));
		user.setOtp(otp);
		user.setOtpGeneratedTime(LocalDateTime.now());
		userService.saveUser(user);

		// Send OTP again
		String subject = "Resend OTP - Taskoria";
		String body = "<h3>Hello " + user.getName() + ",</h3>" + "<p>Your new OTP is: <strong>" + otp + "</strong></p>"
				+ "<p>This OTP is valid for 5 minutes.</p>" + "<p>Regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(user.getEmail(), subject, body);

		redirectAttributes.addFlashAttribute("message", "OTP resent successfully.");
		return "redirect:/auth/verify-otp?email=" + email;
	}

}