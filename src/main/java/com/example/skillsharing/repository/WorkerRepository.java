package com.example.skillsharing.repository;

import com.example.skillsharing.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findBySkillNameContainingIgnoreCase(String skillName);
}
