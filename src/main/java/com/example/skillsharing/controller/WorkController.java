package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import org.springframework.ui.Model; // ✅ Correct

import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.EmailService;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

//import ch.qos.logback.core.model.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class WorkController {

	@Autowired
	private BookingService bookingService;
	@Autowired
	private EmailService emailService;

	@GetMapping("/work/start/{bookingId}")
	public String startWork(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking == null) {
			redirectAttributes.addFlashAttribute("error", "Booking not found.");
			return "redirect:/worker/bookings";
		}

		if (booking.getStatus() == BookingStatus.IN_PROGRESS) {
			redirectAttributes.addFlashAttribute("error", "Work has already started.");
			return "redirect:/worker/bookings";
		}

		booking.setStartTime(LocalDateTime.now());
		booking.setStatus(BookingStatus.IN_PROGRESS);

		bookingService.saveBooking(booking);

		redirectAttributes.addFlashAttribute("success", "Work started successfully.");
		return "redirect:/worker/bookings";
	}

	@GetMapping("/work/finish/{bookingId}")
	public String finishWork(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
		Booking booking = bookingService.getBookingById(bookingId);
		System.out.println("WORK FINISH CALLED for bookingId: " + bookingId);

		if (booking == null) {
			redirectAttributes.addFlashAttribute("error", "Booking not found.");
			return "redirect:/worker/bookings";
		}

		if (!(booking.getStatus() == BookingStatus.IN_PROGRESS
				|| booking.getStatus() == BookingStatus.WAITING_FOR_WORKER)) {
			redirectAttributes.addFlashAttribute("error", "You can't complete work that hasn't started.");
			return "redirect:/worker/bookings";
		}

		if (booking.isWorkerCompleted()) {
			redirectAttributes.addFlashAttribute("error", "You’ve already marked work as completed.");
			return "redirect:/worker/bookings";
		}

		booking.setWorkerCompleted(true);
		if (booking.isRequesterCompleted()) {
			booking.setStatus(BookingStatus.COMPLETED);
			booking.setEndTime(LocalDateTime.now());

			org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext()
					.getAuthentication();
			String email = "";

			if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				email = userDetails.getUsername(); 
			}

			String subject = "We’d love your feedback on your recent experience!";
			String feedbackFormLink = "https://forms.cloud.microsoft/r/fysDh26HKJ";

			String message = "Hello,\n\n"
				    + "Thank you for using Kamiyapp for your recent service (Booking ID: #" + booking.getId() + ").\n\n"
				    + "We are constantly striving to improve our platform, and your feedback would mean a lot to us.\n\n"
				    + "Please take a moment to fill out our short feedback form:\n\n"
				    + feedbackFormLink + "\n\n"
				    + "Your input will help us enhance our services and better meet your needs.\n\n"
				    + "Best regards,\n"
				    + "The Kamiyapp Team\n\n"
				    + "Booking ID: #" + booking.getId() + "\n"
				    + "Feedback Form Link: " + feedbackFormLink;
			emailService.sendEmail(email, subject, message);
		} else {
			booking.setStatus(BookingStatus.WAITING_FOR_REQUESTER); 
		}

		bookingService.saveBooking(booking);

		redirectAttributes.addFlashAttribute("success",
				booking.isRequesterCompleted() ? "Booking completed successfully."
						: "You’ve marked the booking as completed. Waiting for requester to confirm.");

		return "redirect:/worker/bookings";
	}

	@GetMapping("/requester/complete/{bookingId}")
	public String requesterComplete(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking == null) {
			redirectAttributes.addFlashAttribute("error", "Booking not found.");
			return "redirect:/requester/bookings";
		}

		if (!(booking.getStatus() == BookingStatus.IN_PROGRESS
				|| booking.getStatus() == BookingStatus.WAITING_FOR_REQUESTER)) {
			redirectAttributes.addFlashAttribute("error", "Booking cannot be marked complete at this stage.");
			return "redirect:/requester/bookings";
		}

		if (booking.isRequesterCompleted()) {
			redirectAttributes.addFlashAttribute("error", "You’ve already marked this booking as completed.");
			return "redirect:/requester/bookings";
		}

		booking.setRequesterCompleted(true);

		if (booking.isWorkerCompleted()) {
			booking.setStatus(BookingStatus.COMPLETED);
			booking.setEndTime(LocalDateTime.now());

			org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext()
					.getAuthentication();
			String email = "";

			if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				email = userDetails.getUsername(); 
			}

			String subject = "We’d love your feedback on your recent experience!";
			String feedbackFormLink = "https://forms.cloud.microsoft/r/fysDh26HKJ";

			String message = "Hello,\n\n"
				    + "Thank you for using Kamiyapp for your recent service (Booking ID: #" + booking.getId() + ").\n\n"
				    + "We are constantly striving to improve our platform, and your feedback would mean a lot to us.\n\n"
				    + "Please take a moment to fill out our short feedback form:\n\n"
				    + feedbackFormLink + "\n\n"
				    + "Your input will help us enhance our services and better meet your needs.\n\n"
				    + "Best regards,\n"
				    + "The Kamiyapp Team\n\n"
				    + "Booking ID: #" + booking.getId() + "\n"
				    + "Feedback Form Link: " + feedbackFormLink;
			emailService.sendEmail(email, subject, message);
		} else {
			booking.setStatus(BookingStatus.WAITING_FOR_WORKER);
		}

		bookingService.saveBooking(booking);

		redirectAttributes.addFlashAttribute("success", booking.isWorkerCompleted() ? "Booking completed successfully."
				: "You’ve marked the booking as completed. Waiting for worker to confirm.");

		return "redirect:/requester/bookings";
	}

	@GetMapping("/requester/payment/{bookingId}")
	public String showPaymentPage(@PathVariable Long bookingId, Model model) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking != null && booking.getStatus() == BookingStatus.COMPLETED && !booking.isPaid()) {
			Executors.newSingleThreadScheduledExecutor().schedule(() -> {
				booking.setPaid(true);
				booking.setPaymentDate(LocalDateTime.now());
				bookingService.saveBooking(booking);
				System.out.println("Payment auto-marked for booking: " + bookingId);
			}, 30, TimeUnit.SECONDS);
		}

		model.addAttribute("booking", booking);
		return "payment-qr";
	}

}
