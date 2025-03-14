package com.example.skillsharing.controller;

import com.example.skillsharing.model.Worker;
import com.example.skillsharing.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @GetMapping("/workers/search")
    public String searchWorkers(@RequestParam("skill") String skill, Model model) {
        List<Worker> workers = workerService.findWorkersBySkill(skill);
        model.addAttribute("workers", workers);
        return "search-workers";
    }
}
