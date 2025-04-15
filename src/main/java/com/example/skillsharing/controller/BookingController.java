
package com.example.skillsharing.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.InspectionReportService;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;

import jakarta.mail.MessagingException;

@Controller
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@Autowired
	private SkillListingService skillListingService;

	@Autowired

	private InspectionReportService inspectionReportService;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private EmailService emailService;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@GetMapping("/requester/bookings")
	public String showRequesterBookings(Model model, Principal principal) {
		// Get current user (requester) by email
		String email = principal.getName();
		User requester = userService.findByEmail(email);

		// Fetch all bookings made by the requester and sort them by request time
		List<Booking> bookings = bookingService.findByRequesterId(requester.getId());
		bookings.sort((b1, b2) -> b2.getRequestTime().compareTo(b1.getRequestTime())); // Sort by request time, latest
																						// first

		// Map to track if feedback has been given for a booking
		Map<Long, Boolean> feedbackMap = new HashMap<>();

		// Map to track if an inspection report exists for a booking
		Map<Long, Boolean> inspectionReportMap = new HashMap<>();

		for (Booking booking : bookings) {
			// Check if feedback exists for this booking from the current requester
			boolean hasFeedback = feedbackService.getFeedbackByBookingAndReviewer(booking.getId(), requester.getId())
					.isPresent();
			feedbackMap.put(booking.getId(), hasFeedback);

			// Check if an inspection report exists for this booking
			boolean hasReport = inspectionReportService.getReportByBookingId(booking.getId()) != null;
			inspectionReportMap.put(booking.getId(), hasReport);
		}

		// Add data to the model for Thymeleaf view
		model.addAttribute("bookings", bookings);
		model.addAttribute("feedbackMap", feedbackMap);
		model.addAttribute("inspectionReportMap", inspectionReportMap);

		return "requester/bookings";
	}

	@GetMapping("/worker/bookings")
	public String getWorkerBookings(Model model, Principal principal) {
		String email = principal.getName();
		User worker = userService.findByEmail(email);

		List<Booking> bookings = new ArrayList<>();

		if (worker != null) {
			// Fetch bookings that include inspection reports
			bookings = bookingService.getBookingsWithReportsByWorker(worker.getId());

			// Sort by request time (latest first)
			bookings.sort((b1, b2) -> b2.getRequestTime().compareTo(b1.getRequestTime()));

			// Optional: Preprocess or validate requester rating if needed
			for (Booking booking : bookings) {
				User requester = booking.getRequester();
				Double rating = requester != null ? requester.getRating() : null;

				if (rating != null) {
					// Example: Round to 1 decimal place
					requester.setRating(Math.round(rating * 10.0) / 10.0);
				}
			}
		}

		model.addAttribute("bookings", bookings);
		return "worker/bookings";
	}

	@PostMapping("/bookings/create")
	public String createBooking(@RequestParam Long skillId, @RequestParam Long workerId) throws MessagingException {
		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUsername();
		User requester = userService.findByEmail(email);

		SkillListing skillListing = skillListingService.getSkillById(skillId);
		User worker = userService.getUserById(workerId);

		if (requester == null || skillListing == null || worker == null || requester.getId().equals(worker.getId())) {
			return "error";
		}

		// Create and save booking
		Booking booking = new Booking();
		booking.setWorker(worker);
		booking.setRequester(requester);
		booking.setSkillListing(skillListing);
		booking.setStatus(BookingStatus.PENDING);
		booking.setRequestTime(LocalDateTime.now()); // ✅ Store request time

		bookingService.saveBooking(booking);

//		Email Notification

		String requesterSubject = "Your Booking Request Has Been Successfully Created – Taskoria";
		String workerSubject = "You've Received a New Booking Request – Taskoria";

		String requesterBody = "<h3>Hello " + requester.getName() + ",</h3>"
				+ "<p>Your booking request for the service <strong>" + skillListing.getSkillName()
				+ "</strong> has been successfully created on Taskoria.</p>" + "<p>The worker <strong>"
				+ worker.getName() + "</strong> will review and respond to your request shortly.</p>"
				+ "<p>You can track the status of your booking from your dashboard.</p>" + "<br>"
				+ "<p>Thank you for choosing Taskoria!</p>" + "<p>Warm regards,<br><strong>Team Taskoria</strong></p>";

		String workerBody = "<h3>Hello " + worker.getName() + ",</h3>"
				+ "<p>You have received a new booking request for the service <strong>" + skillListing.getSkillName()
				+ "</strong>.</p>" + "<p><strong>Requester:</strong> " + requester.getName() + "<br>"
				+ "<strong>Contact:</strong> " + requester.getEmail() + "</p>"
				+ "<p>Please log in to your Taskoria dashboard to accept or reject the booking.</p>" + "<br>"
				+ "<p>Thanks for being a part of Taskoria!</p>"
				+ "<p>Kind regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(requester.getEmail(), requesterSubject, requesterBody);
		emailService.sendEmail(worker.getEmail(), workerSubject, workerBody);

		return "redirect:/requester/bookings";
	}

	@PostMapping("/bookings/update-status")
	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status)
			throws MessagingException {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking != null) {
			BookingStatus previousStatus = booking.getStatus();
			if (previousStatus != status) {
				booking.setStatus(status);
				if (status == BookingStatus.ACCEPTED) {
					booking.setAcceptanceTime(LocalDateTime.now());
				} else if (status == BookingStatus.IN_PROGRESS) {
					booking.setStartTime(LocalDateTime.now());
				} else if (status == BookingStatus.COMPLETED) {
					booking.setEndTime(LocalDateTime.now());
				}
				bookingService.saveBooking(booking);

				// Prepare Email
				User requester = booking.getRequester();
				User worker = booking.getWorker();
				String subjectRequester = "Your Taskoria Booking Status Has Been Updated";
				String subjectWorker = "A Booking Assigned to You Has Been Updated";

				String bodyRequester = "<h3>Hello " + requester.getName() + ",</h3>"
						+ "<p>The status of your booking with <strong>" + worker.getName()
						+ "</strong> for the service <strong>" + booking.getSkillListing().getSkillName()
						+ "</strong> has been updated to <strong>" + status + "</strong>.</p>"
						+ "<p>You can view all the booking details and progress on your Taskoria dashboard.</p>"
						+ "<br><p>Thank you for choosing Taskoria!</p>"
						+ "<p>Warm regards,<br><strong>Team Taskoria</strong></p>";

				String bodyWorker = "<h3>Hello " + worker.getName() + ",</h3>"
						+ "<p>The status of your assigned booking with <strong>" + requester.getName()
						+ "</strong> for the service <strong>" + booking.getSkillListing().getSkillName()
						+ "</strong> has been updated to <strong>" + status + "</strong>.</p>"
						+ "<p>Please check your Taskoria dashboard for further actions.</p>"
						+ "<br><p>Kind regards,<br><strong>Team Taskoria</strong></p>";

				// Schedule email sending after 10 seconds
				scheduler.schedule(() -> {
					emailService.sendEmail(requester.getEmail(), subjectRequester, bodyRequester);
					emailService.sendEmail(worker.getEmail(), subjectWorker, bodyWorker);
				}, 10, TimeUnit.SECONDS);
			}

			System.out.println("THis is test");

			return "redirect:/worker/bookings";
		}

		return "error";
	}

}
