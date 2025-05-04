package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class WorkController {

	@Autowired
	private BookingService bookingService;

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
			System.out.println("WORK FINISH CALLED for bookingId inside check: " + bookingId);

			redirectAttributes.addFlashAttribute("error", "You can't complete work that hasn't started.");
			return "redirect:/worker/bookings";
		}

		if (booking.isWorkerCompleted()) {
			redirectAttributes.addFlashAttribute("error", "You’ve already marked work as completed.");
			return "redirect:/worker/bookings";
		}

		booking.setWorkerCompleted(true);
		System.out.println("WORK FINISH CALLED for bookingId after setting : " + bookingId);
		if (booking.isRequesterCompleted()) {
			booking.setStatus(BookingStatus.COMPLETED);
			booking.setEndTime(LocalDateTime.now());
		} else {
			booking.setStatus(BookingStatus.WAITING_FOR_REQUESTER); // ✅ NEW: status update
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
		} else {
			booking.setStatus(BookingStatus.WAITING_FOR_WORKER);
		}

		bookingService.saveBooking(booking);

		redirectAttributes.addFlashAttribute("success", booking.isWorkerCompleted() ? "Booking completed successfully."
				: "You’ve marked the booking as completed. Waiting for worker to confirm.");

		return "redirect:/requester/bookings";
	}

}
