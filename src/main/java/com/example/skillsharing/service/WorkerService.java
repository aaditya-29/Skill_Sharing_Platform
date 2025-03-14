package com.example.skillsharing.service;

import com.example.skillsharing.model.Worker;
import com.example.skillsharing.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    public List<Worker> findWorkersBySkill(String skill) {
        return workerRepository.findBySkillNameContainingIgnoreCase(skill);
    }
}
