package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/feedback/{bookingId}")
    public String showFeedbackForm(@PathVariable Long bookingId, Model model) {
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("bookingId", bookingId);
        return "feedback/feedback-form";
    }

    @PostMapping("/feedback/{bookingId}")
    public String submitFeedback(@PathVariable Long bookingId, @ModelAttribute Feedback feedback) {
        Booking booking = bookingService.getBookingById(bookingId);
        User currentUser = getCurrentUser();

        feedback.setBooking(booking);
        feedback.setReviewer(currentUser);

        if (currentUser.getId().equals(booking.getRequester().getId())) {
            feedback.setReviewee(booking.getWorker());
        } else {
            feedback.setReviewee(booking.getRequester());
        }

        feedbackService.saveFeedback(feedback);
        return "redirect:/dashboard";
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userService.findByEmail(email);
    }
}
