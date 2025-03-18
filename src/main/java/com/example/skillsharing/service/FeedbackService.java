package com.example.skillsharing.service;

import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

}
