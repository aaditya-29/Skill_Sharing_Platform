package com.example.skillsharing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String initialInspection;
    private String requiredWork;
    private double estimatedCost;
    private LocalDateTime inspectionTime;

    // Explicit Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getInitialInspection() {
        return initialInspection;
    }

    public void setInitialInspection(String initialInspection) {
        this.initialInspection = initialInspection;
    }

    public String getRequiredWork() {
        return requiredWork;
    }

    public void setRequiredWork(String requiredWork) {
        this.requiredWork = requiredWork;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public LocalDateTime getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(LocalDateTime inspectionTime) {
        this.inspectionTime = inspectionTime;
    }
}
