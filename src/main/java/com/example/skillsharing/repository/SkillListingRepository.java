package com.example.skillsharing.repository;

import com.example.skillsharing.model.SkillListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillListingRepository extends JpaRepository<SkillListing, Long> {
    List<SkillListing> findByWorkerId(Long workerId);
    List<SkillListing> findBySkillNameContainingIgnoreCase(String skillName);
    

}
