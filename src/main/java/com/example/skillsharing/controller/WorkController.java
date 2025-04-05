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

	    if (booking == null) {
	        redirectAttributes.addFlashAttribute("error", "Booking not found.");
	        return "redirect:/worker/bookings";
	    }

	    if (booking.getStatus() == BookingStatus.COMPLETED) {
	        redirectAttributes.addFlashAttribute("error", "Work has already been completed.");
	        return "redirect:/worker/bookings";
	    }

	    booking.setEndTime(LocalDateTime.now());
	    booking.setStatus(BookingStatus.COMPLETED);

	    bookingService.saveBooking(booking);

	    redirectAttributes.addFlashAttribute("success", "Work completed successfully.");
	    return "redirect:/worker/bookings";
	}

}
