package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping("/leave-review")
	public String showFeedbackPage(Model model) {
		User currentUser = getCurrentUser();

		List<Booking> completedBookings = bookingService.findCompletedBookingsForUser(currentUser.getId());

		model.addAttribute("completedBookings", completedBookings);
		model.addAttribute("currentUser", currentUser);
		return "feedback/leave-feedback";
	}

	@PostMapping("/submit")
	public String submitFeedback(@RequestParam Long bookingId, @RequestParam int rating, @RequestParam String comment) {
		User currentUser = getCurrentUser();
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking == null || !"COMPLETED".equals(booking.getStatus().name())) {
			return "redirect:/feedback/leave-review?error=invalid_booking";
		}

		// Ensure only involved users can leave feedback
		if (!booking.getRequester().getId().equals(currentUser.getId())
				&& !booking.getWorker().getId().equals(currentUser.getId())) {
			return "redirect:/feedback/leave-review?error=unauthorized";
		}

		// Check if feedback already exists for this booking from the user
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

		// Determine reviewee based on who is giving feedback
		boolean isRequester = booking.getRequester().getId().equals(currentUser.getId());
		feedback.setReviewee(isRequester ? booking.getWorker() : booking.getRequester());

		feedbackService.saveFeedback(feedback);

		// ✅ Redirect to the correct booking page
		return isRequester ? "redirect:/requester/bookings?success=feedback_submitted"
				: "redirect:/worker/bookings?success=feedback_submitted";
	}

	@GetMapping("/view/{bookingId}")
	public String viewFeedback(@PathVariable Long bookingId, Model model) {
		List<Feedback> feedbackList = feedbackService.getAllFeedbackForBooking(bookingId);

		if (!feedbackList.isEmpty()) {
			model.addAttribute("feedbackList", feedbackList);
			return "feedback/view-feedback";
		} else {
			model.addAttribute("errorMessage", "No feedback found for this booking.");
			return "feedback/view-feedback";
		}
	}

	private User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email;
		if (principal instanceof UserDetails) {
			email = ((UserDetails) principal).getUsername();
		} else {
			email = principal.toString();
		}
		return userService.findByEmail(email);
	}
}
