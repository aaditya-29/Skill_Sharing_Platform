package com.example.skillsharing.repository;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	List<Feedback> findByRevieweeId(Long revieweeId);

	// Find feedback by booking ID and reviewer (to prevent duplicate submissions)
	Optional<Feedback> findByBookingIdAndReviewerId(Long bookingId, Long reviewerId);

	// Find all feedback for a booking (both requester & worker feedback)
	List<Feedback> findAllByBookingId(Long bookingId);

	// ✅ Added method to get all ratings for a user
	@Query("SELECT f.rating FROM Feedback f WHERE f.reviewee.id = :userId")
	List<Double> findRatingsByUserId(@Param("userId") Long userId);

	Optional<Feedback> findByBookingAndReviewee(Booking booking, User reviewee);

	@Query("SELECT f FROM Feedback f WHERE f.reviewee.id = :revieweeId")
	List<Feedback> findReceivedFeedbackByUserId(@Param("revieweeId") Long revieweeId);

}
