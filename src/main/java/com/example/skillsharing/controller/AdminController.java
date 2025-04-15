package com.example.skillsharing.controller;

import com.example.skillsharing.model.*;
import com.example.skillsharing.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private SkillListingService skillListingService;
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        List<Booking> bookings = bookingService.getAllBookings();
        List<SkillListing> skills = skillListingService.getAllSkills();
        List<Feedback> feedbackList = feedbackService.getAllFeedback();

        long totalUsers = users.size();
        long totalWorkers = users.stream().filter(u -> u.getRole() == Role.WORKER).count();
        long totalRequesters = users.stream().filter(u -> u.getRole() == Role.REQUESTER).count();

        long totalBookings = bookings.size();
        long completedBookings = bookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        long inProgressBookings = bookings.stream().filter(b -> b.getStatus() == BookingStatus.IN_PROGRESS).count();

        model.addAttribute("users", users);
        model.addAttribute("bookings", bookings);
        model.addAttribute("skills", skills);
        model.addAttribute("feedbackList", feedbackList);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalWorkers", totalWorkers);
        model.addAttribute("totalRequesters", totalRequesters);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("inProgressBookings", inProgressBookings);

        return "admin/dashboard";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin/dashboard?userDeleted=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=FailedToDeleteUser";
        }
    }

    @PostMapping("/booking/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return "redirect:/admin/dashboard?bookingDeleted=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=FailedToDeleteBooking";
        }
    }

    @PostMapping("/booking/updateStatus")
    public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam String status) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking != null) {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                booking.setStatus(bookingStatus);
                bookingService.updateBooking(booking);
                return "redirect:/admin/dashboard?statusUpdated=true";
            }
            return "redirect:/admin/dashboard?error=BookingNotFound";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/dashboard?error=InvalidStatus";
        }
    }
}
