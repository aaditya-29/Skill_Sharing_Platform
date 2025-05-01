package com.example.skillsharing.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.EmailService;
import com.example.skillsharing.service.FeedbackService;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillListingService skillListingService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FeedbackService feedbackService; // Use FeedbackService for worker ratings

    @GetMapping("/add")
    public String showAddSkillForm(Model model) {
        model.addAttribute("skill", new SkillListing());
        return "skills/add-skill";
    }

    @PostMapping("/add")
    public String addSkill(@ModelAttribute SkillListing skill) throws MessagingException {
        String email = getCurrentUserEmail();
        User worker = userService.findByEmail(email);

        if (worker != null) {
            skill.setWorker(worker);
            skillListingService.saveSkill(skill);

            // Email Notification
            String subject = "Your New Skill Has Been Listed on Kamiyapp";
            String body = "<h3>Hello " + worker.getName() + ",</h3>"
                    + "<p>You've successfully listed a new skill on Kamiyapp.</p>"
                    + "<h4>🛠️ Skill Details:</h4>"
                    + "<p><strong>Skill Name:</strong> " + skill.getSkillName() + "</p>"
                    + "<p><strong>Category:</strong> " + skill.getCategory() + "</p>"
                    + "<p><strong>Location:</strong> " + skill.getLocation() + "</p>"
                    + "<p><strong>Price:</strong> ₹" + skill.getPrice() + "</p>"
                    + "<br><p>Your listing is now visible to requesters searching for skilled professionals like you.</p>"
                    + "<p>Thank you for contributing to the Kamiyapp network!</p>"
                    + "<br><p>Warm regards,<br><strong>Team Kamiyapp</strong></p>";

            emailService.sendEmail(worker.getEmail(), subject, body);

            return "redirect:/worker/dashboard";
        }
        return "error";
    }

    @GetMapping("/worker/dashboard")
    public String workerDashboard(Model model) {
        String email = getCurrentUserEmail();
        User worker = userService.findByEmail(email);

        List<SkillListing> skills = skillListingService.getSkillsByWorker(worker.getId());
        
        // Create a map to hold ratings for each worker
        Map<Long, Double> workerRatings = new HashMap<>();
        for (SkillListing skill : skills) {
            if (skill.getWorker() != null) {
                Long workerId = skill.getWorker().getId();
                Double avgRating = feedbackService.getAverageRatingForUser(workerId); // Fetch ratings using FeedbackService
                workerRatings.put(workerId, avgRating != null ? avgRating : 0.0);
            }
        }

        model.addAttribute("skills", skills);
        model.addAttribute("workerRatings", workerRatings);

        return "worker/dashboard";
    }

    @GetMapping("/search")
    public String searchWorkers(@RequestParam(name = "skill", required = false) String skill, Model model) {
        try {
            if (skill != null && !skill.isEmpty()) {
                List<SkillListing> skills = skillListingService.searchBySkillOrCategory(skill);
                
                // Get ratings for the workers related to these skills
                Map<Long, Double> workerRatings = new HashMap<>();
                for (SkillListing skillListing : skills) {
                    Long workerId = skillListing.getWorker().getId();
                    Double avgRating = feedbackService.getAverageRatingForUser(workerId); // Fetch ratings
                    workerRatings.put(workerId, avgRating != null ? avgRating : 0.0);
                }
                
                model.addAttribute("skills", skills);
                model.addAttribute("workerRatings", workerRatings);
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

    @GetMapping("/all")
    public String listAllSkills(Model model) {
        List<SkillListing> skills = skillListingService.getAllSkills();
        
        // Get ratings for workers associated with these skills
        Map<Long, Double> workerRatings = new HashMap<>();
        for (SkillListing skill : skills) {
            Long workerId = skill.getWorker().getId();
            Double avgRating = feedbackService.getAverageRatingForUser(workerId); // Fetch ratings
            workerRatings.put(workerId, avgRating != null ? avgRating : 0.0);
        }

        model.addAttribute("skills", skills);
        model.addAttribute("workerRatings", workerRatings);

        return "skills/skills"; // Maps to skills-list.html
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }
}
