package com.example.skillsharing.service;

import com.example.skillsharing.model.InspectionReport;
import com.example.skillsharing.repository.InspectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InspectionReportService {

    @Autowired
    private InspectionReportRepository inspectionReportRepository;

    public void saveReport(InspectionReport report) {
        inspectionReportRepository.save(report);
    }

    public InspectionReport getReportByBookingId(Long bookingId) {
        return inspectionReportRepository.findByBookingId(bookingId);
    }
}
