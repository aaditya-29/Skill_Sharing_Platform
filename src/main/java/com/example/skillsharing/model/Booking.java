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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "skill_id")
	private SkillListing skillListing;
	@OneToOne(mappedBy = "booking")
	private InspectionReport inspectionReport;

	private LocalDateTime requestTime; // ✅ Time when requester books
	private LocalDateTime acceptanceTime; // ✅ Time when worker accepts

	private LocalDateTime startTime; // ✅ Time when worker starts work
	private LocalDateTime endTime; // ✅ Time when work is completed
	
	
	
	private LocalDateTime cancellationTime;


	@Column(nullable = false)
	private boolean workerCompleted = false;

	public boolean isWorkerCompleted() {
		return workerCompleted;
	}

	public void setWorkerCompleted(boolean workerCompleted) {
		this.workerCompleted = workerCompleted;
	}

	public boolean isRequesterCompleted() {
		return requesterCompleted;
	}

	public void setRequesterCompleted(boolean requesterCompleted) {
		this.requesterCompleted = requesterCompleted;
	}

	public boolean isFullyCompleted() {
		return this.workerCompleted && this.requesterCompleted;
	}

	@Column(nullable = false)
	private boolean requesterCompleted = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;

	public Booking() {
		this.status = BookingStatus.PENDING; 
	}

	// Getters & Setters

	public LocalDateTime getRequestTime() {
		return requestTime;
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

	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public LocalDateTime getAcceptanceTime() {
		return acceptanceTime;
	}

	public void setAcceptanceTime(LocalDateTime acceptanceTime) {
		this.acceptanceTime = acceptanceTime;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
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

	public InspectionReport getInspectionReport() {
		return inspectionReport;
	}

	public void setInspectionReport(InspectionReport inspectionReport) {
		this.inspectionReport = inspectionReport;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getCancellationTime() {
		return cancellationTime;
	}

	public void setCancellationTime(LocalDateTime cancellationTime) {
		this.cancellationTime = cancellationTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SkillListing getSkillListing() {
		return skillListing;
	}

	public void setSkillListing(SkillListing skillListing) {
		this.skillListing = skillListing;
	}
}
