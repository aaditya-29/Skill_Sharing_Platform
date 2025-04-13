
package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.InspectionReportService;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@GetMapping("/requester/bookings")
	public String showRequesterBookings(Model model, Principal principal) {
	    // Get current user (requester) by email
	    String email = principal.getName();
	    User requester = userService.findByEmail(email);

	    // Fetch all bookings made by the requester and sort them by request time (latest first)
	    List<Booking> bookings = bookingService.findByRequesterId(requester.getId());
	    bookings.sort((b1, b2) -> b2.getRequestTime().compareTo(b1.getRequestTime())); // Sort by request time, latest first

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
	        bookings = bookingService.getBookingsWithReportsByWorker(worker.getId());
	        // Sort bookings by the acceptance time or request time (latest first)
	        bookings.sort((b1, b2) -> b2.getRequestTime().compareTo(b1.getRequestTime())); // Sort by request time, latest first
	    }

	    // Debug check
	    for (Booking booking : bookings) {
	        System.out.println("Booking ID: " + booking.getId());
	        System.out.println("Status: " + booking.getStatus());
	        System.out.println("Has Inspection Report: " + (booking.getInspectionReport() != null));
	    }

	    model.addAttribute("bookings", bookings);
	    return "worker/bookings";
	}

	@PostMapping("/bookings/create")
	public String createBooking(@RequestParam Long skillId, @RequestParam Long workerId) {
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
		return "redirect:/requester/bookings";
	}

	@PostMapping("/bookings/update-status")
	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking != null) {
			booking.setStatus(status);

			if (status == BookingStatus.ACCEPTED) {
				booking.setAcceptanceTime(LocalDateTime.now()); // ✅ Store acceptance time
			}

			bookingService.saveBooking(booking);
			return "redirect:/worker/bookings";
		}

		return "error";
	}

}
