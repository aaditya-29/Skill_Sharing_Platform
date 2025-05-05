package com.example.skillsharing.service;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.Feedback;
import com.example.skillsharing.model.User;
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
		List<Feedback> received = feedbackRepository.findByRevieweeId(userId);
		System.out.println("✅ Received Feedback count: " + received.size());
		for (Feedback f : received) {
			System.out.println("From: " + f.getReviewer().getName() + " ➜ To: " + f.getReviewee().getName());
		}
		return received;
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
		return average.isPresent() ? Math.round(average.getAsDouble() * 10.0) / 10.0 : 0.0;
	}

	public Feedback findByBookingAndReviewee(Booking booking, User reviewee) {
		return feedbackRepository.findByBookingAndReviewee(booking, reviewee).orElse(null);
	}

	public List<Feedback> getAllFeedback() {
		return feedbackRepository.findAll();
	}

	public List<Feedback> getReceivedFeedbackForUser(Long userId) {
		return feedbackRepository.findByRevieweeId(userId);
	}
    public void deleteFeedback(Long id) {
        // Ensure the feedback exists before deletion
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Feedback not found!");
        }
    }



}
