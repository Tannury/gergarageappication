package com.gersgarage.controller;

import com.gersgarage.model.User;
import com.gersgarage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Collection;

@Controller
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Default landing page (either registration or dashboard based on user role)
    @GetMapping("/")
    public String redirectToRegistrationOrDashboard(Principal principal) {
        if (principal != null) {
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/adminDashboard";
            } else {
                return "redirect:/userDashboard";
            }
        }
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // This points to `src/main/resources/templates/register.html`
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        if (!userService.isUsernameAvailable(user.getUsername())) {
            model.addAttribute("usernameError", "Username is not available. Please choose another one.");
            return "register";
        }

        if (!userService.isPhoneAvailable(user.getPhone())) {
            model.addAttribute("phoneError", "Phone number is already in use.");
            return "register";
        }

        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/userDashboard")
    public String userDashboard() {
        return "userDashboard"; // This points to `src/main/resources/templates/userDashboard.html`
    }
}
