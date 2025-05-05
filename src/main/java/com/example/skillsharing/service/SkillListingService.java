package com.example.skillsharing.service;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.repository.SkillListingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillListingService {

	private final SkillListingRepository skillListingRepository;

	public SkillListingService(SkillListingRepository skillListingRepository) {
		this.skillListingRepository = skillListingRepository;
	}


	public void saveSkill(SkillListing skill) {
		skillListingRepository.save(skill);
	}


	public List<SkillListing> getSkillsByWorker(Long workerId) {
		return skillListingRepository.findByWorkerId(workerId);
	}


	public List<SkillListing> getAllSkills() {
		return skillListingRepository.findAll();
	}


	public List<SkillListing> searchBySkillOrCategory(String searchTerm) {
		return skillListingRepository.searchBySkillNameOrCategory(searchTerm);
	}


	public SkillListing getSkillById(Long skillId) {
		return skillListingRepository.findById(skillId).orElse(null);
	}


	public List<SkillListing> findRandomWorkersWithSkills() {
		Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
		return skillListingRepository.findAll(pageable).getContent();
	}
	public void deleteSkill(Long id) {
        // Handle the case when there are bookings that depend on this skill
        if (skillListingRepository.existsById(id)) {
            skillListingRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Skill not found!");
        }
    }

}
