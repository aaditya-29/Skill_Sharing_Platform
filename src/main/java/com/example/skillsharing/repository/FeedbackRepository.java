package com.example.skillsharing.repository;

import com.example.skillsharing.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	List<Feedback> findByRevieweeId(Long revieweeId);

	// Find feedback by booking ID and reviewer (to prevent duplicate submissions)
	Optional<Feedback> findByBookingIdAndReviewerId(Long bookingId, Long reviewerId);

	// Find all feedback for a booking (both requester & worker feedback)
	List<Feedback> findAllByBookingId(Long bookingId);
}
