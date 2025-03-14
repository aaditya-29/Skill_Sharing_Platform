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

    public void saveSkill(SkillListing skill) {
        skillListingRepository.save(skill);
    }

    public List<SkillListing> getSkillsByWorker(Long workerId) {
        return skillListingRepository.findByWorkerId(workerId);
    }

    public List<SkillListing> getAllSkills() {
        return skillListingRepository.findAll();
    }

    public List<SkillListing> findBySkillNameContaining(String skill) {
        return skillListingRepository.findBySkillNameContainingIgnoreCase(skill);
    }
}
