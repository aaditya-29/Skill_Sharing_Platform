package com.example.skillsharing.repository;

import com.example.skillsharing.model.SkillListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillListingRepository extends JpaRepository<SkillListing, Long> {
	
	List<SkillListing> findByWorkerId(Long workerId);

	@Query("SELECT s FROM SkillListing s " +
	       "WHERE LOWER(s.skillName) LIKE LOWER(CONCAT('%', :term, '%')) " +
	       "   OR LOWER(s.category) LIKE LOWER(CONCAT('%', :term, '%'))")
	List<SkillListing> searchBySkillNameOrCategory(@Param("term") String term);
}
