package com.example.skillsharing.service;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.repository.SkillListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillListingService {

	@Autowired
	private SkillListingRepository skillListingRepository;

	/**
	 * Save a skill listing
	 */
	public void saveSkill(SkillListing skill) {
		skillListingRepository.save(skill);
	}

	/**
	 * Get all skills listed by a specific worker
	 */
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
}
