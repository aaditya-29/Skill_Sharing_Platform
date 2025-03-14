package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.InspectionReport;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.InspectionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InspectionController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private InspectionReportService inspectionReportService;

    @GetMapping("/inspection/{bookingId}")
    public String showInspectionForm(@PathVariable Long bookingId, Model model) {
        model.addAttribute("report", new InspectionReport());
        model.addAttribute("bookingId", bookingId);
        return "inspection/inspection-form";
    }

    @PostMapping("/inspection/{bookingId}")
    public String submitInspection(@PathVariable Long bookingId, @ModelAttribute InspectionReport report) {
        Booking booking = bookingService.getBookingById(bookingId);
        report.setBooking(booking);
        report.setInspectionTime(java.time.LocalDateTime.now());
        inspectionReportService.saveReport(report);

        booking.setStatus(BookingStatus.INSPECTION_DONE);
        bookingService.saveBooking(booking);

        return "redirect:/worker/dashboard";
    }
}
