
package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.InspectionReport;
import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
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
import java.util.List;

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

	@GetMapping("/requester/bookings")
	public String showRequesterBookings(Model model, Principal principal) {
		String email = principal.getName();
		User requester = userService.findByEmail(email);

		if (requester != null) {
			List<Booking> bookings = bookingService.getBookingsByRequester(requester.getId());
			model.addAttribute("bookings", bookings);
		} else {
			model.addAttribute("bookings", new ArrayList<>());
		}

		return "requester/bookings";
	}

	@GetMapping("/worker/bookings")
	public String getWorkerBookings(Model model, Principal principal) {
		String email = principal.getName();
		User worker = userService.findByEmail(email);

		if (worker != null) {
			List<Booking> bookings = bookingService.getBookingsByWorker(worker.getId());
			model.addAttribute("bookings", bookings);
		} else {
			model.addAttribute("bookings", new ArrayList<>());
		}

		return "worker/bookings";
	}

	@PostMapping("/bookings/create")
	public String createBooking(@RequestParam Long skillId, @RequestParam Long workerId, @RequestParam String startTime,
			@RequestParam String endTime) {
		// Get the logged-in user's email
		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUsername();
		User requester = userService.findByEmail(email);

		// Retrieve skill listing and worker
		SkillListing skillListing = skillListingService.getSkillById(skillId);
		User worker = userService.getUserById(workerId);

		// Validation: Ensure requester and worker are not the same
		if (requester == null || skillListing == null || worker == null || requester.getId().equals(worker.getId())) {
			System.out.println("⚠️ Requester and Worker cannot be the same user!");
			return "error"; // Redirect to an error page or handle it gracefully
		}

		// Parse start and end times
		LocalDateTime parsedStartTime = LocalDateTime.parse(startTime);
		LocalDateTime parsedEndTime = LocalDateTime.parse(endTime);

		// Create and save booking
		Booking booking = new Booking();
		booking.setWorker(worker);
		booking.setRequester(requester);
		booking.setSkillListing(skillListing);
		booking.setStartTime(parsedStartTime);
		booking.setEndTime(parsedEndTime);
		booking.setStatus(BookingStatus.PENDING);

		bookingService.saveBooking(booking);
		return "redirect:/requester/bookings";
	}

	@PostMapping("/bookings/update-status")
	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status,
			Principal principal) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking != null) {
			booking.setStatus(status);
			bookingService.saveBooking(booking);
			return "redirect:/worker/bookings";
		}

		return "error";
	}

//	@PostMapping("/inspection/submit/{bookingId}")
//	public String submitInspectionReport(@PathVariable Long bookingId, @ModelAttribute InspectionReport report,
//			Principal principal) {
//		Booking booking = bookingService.getBookingById(bookingId);
//
//		if (booking == null || !booking.getWorker().getEmail().equals(principal.getName())) {
//			return "error"; // Prevent unauthorized access
//		}
//
//		// Set booking and timestamp
//		report.setBooking(booking);
//		report.setInspectionTime(LocalDateTime.now());
//
//		// Save report
//		inspectionReportService.saveReport(report);
//
//		// Update booking status
//		booking.setStatus(BookingStatus.INSPECTION_DONE);
//		bookingService.saveBooking(booking);
//
//		return "redirect:/worker/bookings";
//	}

//	@GetMapping("/inspection/view/{bookingId}")
//	public String viewInspectionReport(@PathVariable Long bookingId, Model model, Principal principal) {
//		Booking booking = bookingService.getBookingById(bookingId);
//
//		if (booking == null || (!booking.getRequester().getEmail().equals(principal.getName())
//				&& !booking.getWorker().getEmail().equals(principal.getName()))) {
//			return "error"; // Prevent unauthorized access
//		}
//
//		InspectionReport report = booking.getInspectionReport();
//		model.addAttribute("report", report);
//
//		return "inspection/view";
//	}

}
