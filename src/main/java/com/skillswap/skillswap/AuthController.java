
package com.skillswap.skillswap;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class AuthController {

    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        if (fullName.isBlank() || email.isBlank()
                || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute(
                    "error", "Password must contain at least 8 characters.");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute(
                    "error", "This email is already registered.");
            return "register";
        }

        AppUser user = new AppUser();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        AppUser user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user == null
                || !passwordEncoder.matches(
                        password, user.getPassword())) {
            model.addAttribute(
                    "error", "Invalid email or password.");
            return "login";
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getFullName());

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "userName", session.getAttribute("userName"));

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
