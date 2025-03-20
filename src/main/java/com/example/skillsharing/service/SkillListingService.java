package com.example.skillsharing.service;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.repository.SkillListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillListingService {

	private final SkillListingRepository skillListingRepository;

	// ✅ Constructor-based dependency injection (Recommended)
	@Autowired
	public SkillListingService(SkillListingRepository skillListingRepository) {
		this.skillListingRepository = skillListingRepository;
	}

	/**
	 * Save a skill listing
	 */
	public void saveSkill(SkillListing skill) {
		skillListingRepository.save(skill);
	}

	public List<SkillListing> getSkillsByWorker(Long workerId) {
		return skillListingRepository.findByWorkerId(workerId);
	}

	/**
	 * Get all skills
	 */
	public List<SkillListing> getAllSkills() {
		return skillListingRepository.findAll();
	}

	/**
	 * Search skills by name (case-insensitive)
	 */
	public List<SkillListing> findBySkillNameContaining(String skill) {
		return skillListingRepository.findBySkillNameContainingIgnoreCase(skill);
	}

	/**
	 * Find a skill by ID
	 */
	public SkillListing getSkillById(Long skillId) {
		return skillListingRepository.findById(skillId).orElse(null);
	}

	/**
	 * Find random workers with skills (Limited to 10)
	 */
	public List<SkillListing> findRandomWorkersWithSkills() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
		return skillListingRepository.findAll(pageable).getContent();
	}
}
