// src/main/java/com/wedding/broker/controller/AuthController.java
package com.wedding.broker.controller;

import com.wedding.broker.model.User; // Ensure this import is for your actual User entity
import com.wedding.broker.repository.UserRepository;
import com.wedding.broker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Still needed for the GET mapping if you want to use it
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    // You might not need UserRepository and PasswordEncoder directly injected here
    // if AuthService handles all user creation/password encoding.
    // However, for existsByUsername/Email checks before calling AuthService, they could be useful.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // This might be used internally by AuthService, or if you had direct User saving here.

    @Autowired
    private AuthService authService; // Keep this for the email verification flow

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // This is good for displaying the form.
        // It provides an empty User object for the form fields.
        model.addAttribute("user", new User());
        return "register";
    }

    // THIS IS THE @PostMapping("/register") METHOD TO KEEP AND USE
    // It delegates to AuthService for user registration and verification.
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            // It's good practice to do basic checks here before calling the service
            // though AuthService should also validate inputs.
            if (userRepository.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                return "redirect:/register";
            }
            if (userRepository.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email already registered.");
                return "redirect:/register";
            }

            authService.registerUser(username, password, email);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please check your email and enter the verification code below.");
            return "redirect:/verify-email?email=" + email;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    // Show verification form
    @GetMapping("/verify-email")
    public String showVerifyEmailForm(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email); // Pre-fill email if provided
        return "verify-email"; // Assumes you have a verify-email.html
    }

    // Handle email verification
    @PostMapping("/verify-email")
    public String verifyEmail(
            @RequestParam String email,
            @RequestParam String code,
            RedirectAttributes redirectAttributes) {
        try {
            authService.verifyUser(email, code);
            redirectAttributes.addFlashAttribute("message", "Email verification successful! You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("email", email); // Keep email in URL for retry
            return "redirect:/verify-email";
        }
    }

    // Handle resending verification code
    @PostMapping("/resend-verification")
    public String resendVerification(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            authService.resendVerificationCode(email);
            redirectAttributes.addFlashAttribute("message", "A new verification code has been sent to your email.");
            return "redirect:/verify-email"; // Redirect back to verification page
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("email", email);
            return "redirect:/verify-email";
        }
    }
}