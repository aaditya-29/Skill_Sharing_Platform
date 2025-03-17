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

	/**
	 * Show feedback page where user can review completed bookings.
	 */
	@GetMapping("/leave-review")
	public String showFeedbackPage(Model model) {
		User currentUser = getCurrentUser();

		// Fetch only COMPLETED bookings where the user was involved
		List<Booking> completedBookings = bookingService.findCompletedBookingsForUser(currentUser.getId());

		// Debugging Output
		System.out.println("Current logged-in user: " + currentUser.getName() + " (ID: " + currentUser.getId() + ")");
		System.out.println("Completed bookings available for feedback:");
		completedBookings.forEach(b -> System.out.println("Booking ID: " + b.getId() + ", Requester: "
				+ b.getRequester().getName() + ", Worker: " + b.getWorker().getName()));

		model.addAttribute("completedBookings", completedBookings);
		model.addAttribute("currentUser", currentUser);
		return "feedback/leave-feedback";
	}

	/**
	 * Submit feedback for a completed booking.
	 */
	@PostMapping("/submit")
	public String submitFeedback(@RequestParam Long bookingId, @RequestParam int rating, @RequestParam String comment) {
		User currentUser = getCurrentUser();

		System.out.println("🔎 Submitting feedback for Booking ID: " + bookingId);

		Booking booking = bookingService.getBookingById(bookingId);

		if (booking == null) {
			System.out.println("❌ ERROR: Booking not found for ID: " + bookingId);
			return "redirect:/feedback/leave-review?error=invalid_booking";
		}

		// **FIX: Convert Enum to String before checking**
		String bookingStatus = booking.getStatus().name(); // Convert Enum to String

		System.out.println("📌 Booking ID: " + bookingId + " - Status Retrieved: " + bookingStatus);

		if (!"COMPLETED".equals(bookingStatus)) {
			System.out.println("❌ ERROR: Booking " + bookingId + " is not completed.");
			return "redirect:/feedback/leave-review?error=invalid_booking";
		}

		if (!booking.getRequester().getId().equals(currentUser.getId())
				&& !booking.getWorker().getId().equals(currentUser.getId())) {
			System.out.println(
					"❌ ERROR: User " + currentUser.getId() + " is not authorized to review booking " + bookingId);
			return "redirect:/feedback/leave-review?error=unauthorized";
		}

		Feedback feedback = new Feedback();
		feedback.setBooking(booking);
		feedback.setReviewer(currentUser);
		feedback.setRating(rating);
		feedback.setComment(comment);
		feedback.setReviewee(booking.getRequester().getId().equals(currentUser.getId()) ? booking.getWorker()
				: booking.getRequester());

		feedbackService.saveFeedback(feedback);

		System.out.println("✅ Feedback submitted successfully for Booking ID: " + bookingId);
		return "redirect:/requester/bookings?success=feedback_submitted";
	}

	/**
	 * Fetch the currently logged-in user.
	 */
	private User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email;
		if (principal instanceof UserDetails) {
			email = ((UserDetails) principal).getUsername();
		} else {
			email = principal.toString();
		}

		User user = userService.findByEmail(email);
		System.out.println("Current logged-in user: " + user.getName() + " (ID: " + user.getId() + ")");
		return user;
	}

	@GetMapping("/view/{bookingId}")
	public String viewFeedback(@PathVariable Long bookingId, Model model) {
		Optional<Feedback> feedback = feedbackService.getFeedbackByBookingId(bookingId);

		if (feedback.isPresent()) {
			model.addAttribute("feedback", feedback.get());
			return "feedback/view-feedback";
		} else {
			model.addAttribute("errorMessage", "No feedback found for this booking.");
			return "feedback/view-feedback";
		}
	}
}
