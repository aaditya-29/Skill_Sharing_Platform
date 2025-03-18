package com.example.skillsharing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", uniqueConstraints = { @UniqueConstraint(columnNames = { "booking_id", "reviewer_id" }) // Allow
																													// 2
																													// feedback
																													// per
																													// booking
})
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@ManyToOne
	@JoinColumn(name = "reviewer_id", nullable = false)
	private User reviewer; // The person giving feedback

	@ManyToOne
	@JoinColumn(name = "reviewee_id", nullable = false)
	private User reviewee; // The person receiving feedback

	@Column(nullable = false)
	private int rating;

	@Column(length = 500)
	private String comment;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	// Getters and Setters

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

	public User getReviewer() {
		return reviewer;
	}

	public void setReviewer(User reviewer) {
		this.reviewer = reviewer;
	}

	public User getReviewee() {
		return reviewee;
	}

	public void setReviewee(User reviewee) {
		this.reviewee = reviewee;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
