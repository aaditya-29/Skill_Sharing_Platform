package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;

	// 🟢 Show Admin Dashboard
	@GetMapping("/dashboard")
	public String adminDashboard(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("bookings", bookingService.getAllBookings());
		return "admin/dashboard";
	}

	// 🟢 Delete User (Admin Control)
	@PostMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "redirect:/admin/dashboard?userDeleted"; // ✅ Redirect after deletion
	}

	// ❌ Removed updateUserRole() since you don't want role changes

	// 🟢 Delete Booking (Admin Control)
	@PostMapping("/booking/delete/{id}")
	public String deleteBooking(@PathVariable Long id) {
		bookingService.deleteBooking(id);
		return "redirect:/admin/dashboard?bookingDeleted"; // ✅ Redirect after deletion
	}

	// 🟢 Update Booking Status (Fix: Convert String to Enum)
	@PostMapping("/booking/updateStatus")
	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam String status) {
		Booking booking = bookingService.getBookingById(bookingId);
		if (booking != null) {
			try {
				BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase()); // ✅ Convert String to Enum
				booking.setStatus(bookingStatus);
				bookingService.updateBooking(booking);
			} catch (IllegalArgumentException e) {
				return "redirect:/admin/dashboard?error=InvalidStatus"; // 🚨 Handle invalid status
			}
		}
		return "redirect:/admin/dashboard?statusUpdated"; // ✅ Redirect after update
	}
}
