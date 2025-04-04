package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@Controller
public class WorkController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/work/start/{bookingId}")
    public String startWork(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
//        booking.setStartTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingService.saveBooking(booking);
        return "redirect:/worker/dashboard";
    }

    @GetMapping("/work/finish/{bookingId}")
    public String finishWork(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
//        booking.setEndTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.COMPLETED);
        bookingService.saveBooking(booking);
        return "redirect:/worker/dashboard";
    }
}
