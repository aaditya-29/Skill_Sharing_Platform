package com.example.skillsharing.controller;

import com.example.skillsharing.model.Role;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/auth/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/auth/register")
    public String registerUser(@ModelAttribute User user) {
        if (user.getRole() == Role.WORKER) {
            user.setRating(0.0);
        }

        // Save user only once before trying to fetch
        userService.saveUser(user);
        System.out.println("User saved with encoded password: " + user.getPassword());

        return "redirect:/auth/login";
    }

    @GetMapping("/auth/login")
    public String showLoginForm() {
        return "user/login";
    }

    @PostMapping("/auth/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        User dbUser = userService.findByEmail(user.getEmail());

        if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            model.addAttribute("error", "Invalid email or password.");
            return "user/login";
        }

        return "redirect:/dashboard"; // ✅ Adjust this as needed
    }
}
