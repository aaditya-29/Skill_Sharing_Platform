package com.example.skillsharing.service;

import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class FeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepository;

	public void saveFeedback(Feedback feedback) {
		feedbackRepository.save(feedback);
	}

	public List<Feedback> getFeedbackForUser(Long userId) {
		return feedbackRepository.findByRevieweeId(userId);
	}

	public Optional<Feedback> getFeedbackByBookingAndReviewer(Long bookingId, Long reviewerId) {
		return feedbackRepository.findByBookingIdAndReviewerId(bookingId, reviewerId);
	}

	public List<Feedback> getAllFeedbackForBooking(Long bookingId) {
		return feedbackRepository.findAllByBookingId(bookingId);
	}

	public double getAverageRatingForUser(Long userId) {
		List<Double> ratings = feedbackRepository.findRatingsByUserId(userId);

		OptionalDouble average = ratings.stream().mapToDouble(Double::doubleValue).average();

		return average.isPresent() ? average.getAsDouble() : 0.0; // Return 0 if no ratings
	}
}
