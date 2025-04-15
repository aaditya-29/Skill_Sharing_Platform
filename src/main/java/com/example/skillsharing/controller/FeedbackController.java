package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.UserService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@GetMapping("/leave-review")
	public String showFeedbackPage(Model model) {
		User currentUser = getCurrentUser();

		// Fetch all completed bookings for the current user
		List<Booking> completedBookings = bookingService.findCompletedBookingsForUser(currentUser.getId());

		// Filter out bookings already reviewed by the current user
		List<Booking> eligibleBookings = new ArrayList<>();
		for (Booking booking : completedBookings) {
			boolean alreadyReviewed = feedbackService
					.getFeedbackByBookingAndReviewer(booking.getId(), currentUser.getId()).isPresent();
			if (!alreadyReviewed) {
				eligibleBookings.add(booking);
			}
		}

		model.addAttribute("completedBookings", eligibleBookings);
		model.addAttribute("currentUser", currentUser);
		return "feedback/leave-feedback";
	}

	@PostMapping("/submit")
	public String submitFeedback(@RequestParam Long bookingId, @RequestParam int rating, @RequestParam String comment)
			throws MessagingException {
		User currentUser = getCurrentUser();
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking == null || !"COMPLETED".equals(booking.getStatus().name())) {
			return "redirect:/feedback/leave-review?error=invalid_booking";
		}

		if (!booking.getRequester().getId().equals(currentUser.getId())
				&& !booking.getWorker().getId().equals(currentUser.getId())) {
			return "redirect:/feedback/leave-review?error=unauthorized";
		}

		Optional<Feedback> existingFeedback = feedbackService.getFeedbackByBookingAndReviewer(bookingId,
				currentUser.getId());
		if (existingFeedback.isPresent()) {
			return "redirect:/feedback/leave-review?error=already_reviewed";
		}

		Feedback feedback = new Feedback();
		feedback.setBooking(booking);
		feedback.setReviewer(currentUser);
		feedback.setRating(rating);
		feedback.setComment(comment);

		boolean isRequester = booking.getRequester().getId().equals(currentUser.getId());
		User reviewee = isRequester ? booking.getWorker() : booking.getRequester();

		feedback.setReviewee(reviewee);

		feedbackService.saveFeedback(feedback);

		// Email Notifications
		String subject = "You've Received a New Feedback on Taskoria";

		String body = "<h3>Hello " + reviewee.getName() + ",</h3>"
				+ "<p>You’ve just received a new feedback from <strong>" + currentUser.getName()
				+ "</strong> regarding your recent booking.</p>" + "<p><strong>Rating:</strong> " + rating + " &#11088;</p>"
				+ "<p><strong>Comment:</strong> \"" + comment + "\"</p>"
				+ "<p>We hope this helps you continue to grow and improve on Taskoria.</p>"
				+ "<p>You can log into your dashboard to view this and other feedback.</p>"
				+ "<br><p>Keep up the great work!</p>" + "<p>Warm regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(reviewee.getEmail(), subject, body);

		return "redirect:/feedback/leave-review?success=feedback_submitted";
	}

	// ✅ Updated to show only feedback where current user is reviewee
	@GetMapping("/view")
	public String viewFeedback(Model model, Principal principal) {
		User currentUser = userService.findByEmail(principal.getName());

		List<Feedback> feedbackList = feedbackService.getFeedbackForUser(currentUser.getId());

		model.addAttribute("feedbackList", feedbackList);
		return "feedback/view-feedback";
	}

	private User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername()
				: principal.toString();
		return userService.findByEmail(email);
	}
}
