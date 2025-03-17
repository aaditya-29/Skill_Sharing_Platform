package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.InspectionReport;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.InspectionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/inspection")
public class InspectionController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private InspectionReportService inspectionReportService;

	// Show Inspection Report Form
	@GetMapping("/submit/{bookingId}")
	public String showInspectionForm(@PathVariable Long bookingId, Model model) {
		model.addAttribute("bookingId", bookingId);
		model.addAttribute("report", new InspectionReport());
		return "inspection/inspection-form";
	}

	// Handle Inspection Report Submission
	@PostMapping("/submit/{bookingId}")
	public String submitInspectionReport(@PathVariable Long bookingId, @ModelAttribute InspectionReport report,
			Principal principal) {
		Booking booking = bookingService.getBookingById(bookingId);

		// Ensure booking exists and is assigned to the logged-in worker
		if (booking == null || !booking.getWorker().getEmail().equals(principal.getName())) {
			return "error"; // Prevent unauthorized access
		}

		// Save Inspection Report
		report.setBooking(booking);
		report.setInspectionTime(LocalDateTime.now());
		inspectionReportService.saveReport(report);

		// Update booking status
		booking.setStatus(BookingStatus.INSPECTION_DONE);
		bookingService.saveBooking(booking);

		return "redirect:/worker/bookings";
	}

	// View Inspection Report
	@GetMapping("/view/{bookingId}")
	public String viewInspectionReport(@PathVariable Long bookingId, Model model, Principal principal) {
		Booking booking = bookingService.getBookingById(bookingId);

		// Prevent unauthorized access
		if (booking == null || (!booking.getRequester().getEmail().equals(principal.getName())
				&& !booking.getWorker().getEmail().equals(principal.getName()))) {
			return "error";
		}

		InspectionReport report = inspectionReportService.getReportByBookingId(bookingId);
		if (report == null) {
			return "error"; // Handle if no report exists
		}

		model.addAttribute("report", report);
		return "inspection/inspection-report";
	}
}
