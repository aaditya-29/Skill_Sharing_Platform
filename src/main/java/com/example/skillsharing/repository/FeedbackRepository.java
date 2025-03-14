package com.example.skillsharing.repository;

import com.example.skillsharing.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRevieweeId(Long revieweeId);
}
