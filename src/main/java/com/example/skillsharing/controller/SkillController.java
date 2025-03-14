package com.example.skillsharing.controller;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class SkillController {

    @Autowired
    private SkillListingService skillListingService;

    @Autowired
    private UserService userService;

    @GetMapping("/skills/add")
    public String showAddSkillForm(Model model) {
        model.addAttribute("skill", new SkillListing());
        return "skills/add-skill";
    }

    @PostMapping("/skills/add")
    public String addSkill(@ModelAttribute SkillListing skill) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User worker = userService.findByEmail(email);

        if (worker != null) {
            skill.setWorker(worker);
            skillListingService.saveSkill(skill);
            return "redirect:/worker/dashboard";
        }
        return "error";
    }

    @GetMapping("/worker/dashboard")
    public String workerDashboard(Model model) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User worker = userService.findByEmail(email);

        model.addAttribute("skills", skillListingService.getSkillsByWorker(worker.getId()));
        return "worker/dashboard";
    }

    @GetMapping("/skills/search")
    public String searchWorkers(@RequestParam(name = "skill", required = false) String skill, Model model) {
        try {
            System.out.println("Search term: " + skill); // Debug log
            if (skill != null && !skill.isEmpty()) {
                List<SkillListing> skills = skillListingService.findBySkillNameContaining(skill);
                System.out.println("Skills found: " + skills.size()); // Debug log
                for (SkillListing s : skills) {
                    System.out.println("Skill: " + s.getSkillName() + ", Worker: " + (s.getWorker() != null ? s.getWorker().getName() : "No worker"));
                }
                model.addAttribute("skills", skills);
            } else {
                model.addAttribute("skills", List.of());
            }
            return "search-workers";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error-page";
        }
    }
}
