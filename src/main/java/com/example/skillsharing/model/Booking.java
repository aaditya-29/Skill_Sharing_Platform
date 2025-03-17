package com.example.skillsharing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worker_id")
	private User worker;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requester_id")
	private User requester;

	@ManyToOne(fetch = FetchType.EAGER) // ✅ Link Booking to SkillListing
	@JoinColumn(name = "skill_id")
	private SkillListing skillListing; // ✅ Update to SkillListing

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
	private InspectionReport inspectionReport;

	// Getter & Setter
	public InspectionReport getInspectionReport() {
		return inspectionReport;
	}

	public void setInspectionReport(InspectionReport inspectionReport) {
		this.inspectionReport = inspectionReport;
	}

	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	// ✅ Getters and Setters for SkillListing
	public SkillListing getSkillListing() {
		return skillListing;
	}

	public void setSkillListing(SkillListing skillListing) {
		this.skillListing = skillListing;
	}

	// ✅ Other Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getWorker() {
		return worker;
	}

	public void setWorker(User worker) {
		this.worker = worker;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}
}
