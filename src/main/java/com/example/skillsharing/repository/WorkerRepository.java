package com.example.skillsharing.repository;

import com.example.skillsharing.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    
    @Query("SELECT DISTINCT s.worker FROM SkillListing s WHERE LOWER(s.skillName) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Worker> findBySkillNameContainingIgnoreCase(@Param("skill") String skill);

    @Query("SELECT DISTINCT s.worker FROM SkillListing s WHERE LOWER(s.category) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Worker> findByCategoryContainingIgnoreCase(@Param("category") String category);
}
