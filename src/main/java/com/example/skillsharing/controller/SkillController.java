package com.example.skillsharing.controller;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillListingService skillListingService;

    @Autowired
    private UserService userService;

    /**
     * Show form to add a skill
     */
    @GetMapping("/add")
    public String showAddSkillForm(Model model) {
        model.addAttribute("skill", new SkillListing());
        return "skills/add-skill";
    }

    /**
     * Handle skill addition
     */
    @PostMapping("/add")
    public String addSkill(@ModelAttribute SkillListing skill) {
        String email = getCurrentUserEmail();
        User worker = userService.findByEmail(email);

        if (worker != null) {
            skill.setWorker(worker);
            skillListingService.saveSkill(skill);
            return "redirect:/worker/dashboard";
        }
        return "error";
    }

    /**
     * Show all skills listed by a specific worker
     */
    @GetMapping("/worker/dashboard")
    public String workerDashboard(Model model) {
        String email = getCurrentUserEmail();
        User worker = userService.findByEmail(email);

        model.addAttribute("skills", skillListingService.getSkillsByWorker(worker.getId()));
        return "worker/dashboard";
    }

    /**
     * Search for skills by skill name
     */
    @GetMapping("/search")
    public String searchWorkers(@RequestParam(name = "skill", required = false) String skill, Model model) {
        try {
            if (skill != null && !skill.isEmpty()) {
                List<SkillListing> skills = skillListingService.findBySkillNameContaining(skill);
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

    /**
     * List all skills
     */
    @GetMapping("/all")
    public String listAllSkills(Model model) {
        List<SkillListing> skills = skillListingService.getAllSkills();
        model.addAttribute("skills", skills);
        return "skills/skills"; // Maps to skills-list.html
    }

    /**
     * Helper method to get the logged-in user's email
     */
    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }
}
