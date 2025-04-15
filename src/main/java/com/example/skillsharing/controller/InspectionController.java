package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.InspectionReport;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.InspectionReportService;

import jakarta.mail.MessagingException;

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

	@Autowired
	private EmailService emailService;

	// Show Inspection Report Form
	@GetMapping("/submit/{bookingId}")
	public String showInspectionForm(@PathVariable Long bookingId, Model model, Principal principal) {
		Booking booking = bookingService.getBookingById(bookingId);

		// Ensure the booking exists and is assigned to the logged-in worker
		if (booking == null || !booking.getWorker().getEmail().equals(principal.getName())) {
			return "error"; // Unauthorized access
		}

		// Check if an inspection report already exists
		boolean inspectionDone = (inspectionReportService.getReportByBookingId(bookingId) != null);

		model.addAttribute("bookingId", bookingId);
		model.addAttribute("report", new InspectionReport());
		model.addAttribute("inspectionDone", inspectionDone); // Pass to the template

		return "inspection/inspection-form";
	}

	// Handle Inspection Report Submission
	@PostMapping("/submit/{bookingId}")
	public String submitInspectionReport(@PathVariable Long bookingId, @ModelAttribute InspectionReport report,
			Principal principal, Model model) throws MessagingException {
		Booking booking = bookingService.getBookingById(bookingId);

		// Ensure booking exists and is assigned to the logged-in worker
		if (booking == null || !booking.getWorker().getEmail().equals(principal.getName())) {
			return "error"; // Prevent unauthorized access
		}

		// Check if an inspection report already exists
		if (inspectionReportService.getReportByBookingId(bookingId) != null) {
			model.addAttribute("error", "Inspection report has already been submitted for this booking.");
			return "redirect:/worker/bookings";
		}

		// Save Inspection Report
		report.setBooking(booking);
		report.setInspectionTime(LocalDateTime.now());
		inspectionReportService.saveReport(report);

		// Update booking status
		booking.setStatus(BookingStatus.INSPECTION_DONE);
		bookingService.saveBooking(booking);

		// Email Notification
		User requester = booking.getRequester();
		User worker = booking.getWorker();

		String subject = "Inspection Report Submitted for Your Taskoria Booking";

		String body = "<h3>Hello " + requester.getName() + ",</h3>"
				+ "<p>We wanted to inform you that your assigned worker <strong>" + worker.getName()
				+ "</strong> has submitted an inspection report for your booking.</p>"
				+ "<h4> Inspection Summary:</h4>" + "<p><strong>Initial Inspection:</strong> "
				+ report.getInitialInspection() + "</p>" + "<p><strong>Required Work:</strong> "
				+ report.getRequiredWork() + "</p>" + "<p><strong>Estimated Cost:</strong> Rs."
				+ report.getEstimatedCost() + "</p>" + "<p><strong>Inspection Time:</strong> "
				+ report.getInspectionTime().toString().replace("T", " ") + "</p>"
				+ "<br><p>Please log in to Taskoria to proceed with the work based on this inspection report.</p>"
				+ "<p>Thank you for using Taskoria!</p>" + "<br><p>Warm regards,<br><strong>Team Taskoria</strong></p>";

		emailService.sendEmail(requester.getEmail(), subject, body);

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
