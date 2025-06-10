package com.wedding.broker.service;

import com.wedding.broker.model.User;
import com.wedding.broker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID; // For generating unique codes

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User registerUser(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // Encode password
        newUser.setEmail(email);

        // Generate verification code and set expiry
        String verificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Simple 6-char code
        newUser.setVerificationCode(verificationCode);
        newUser.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15)); // Code valid for 15 minutes
        newUser.setEnabled(false); // User is not enabled until verified

        userRepository.save(newUser);

        // Send verification email
        emailService.sendVerificationEmail(newUser.getEmail(), newUser.getUsername(), verificationCode);

        return newUser;
    }

    public boolean verifyUser(String email, String verificationCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or verification code."));

        if (user.isEnabled()) {
            return true; // Already verified
        }

        if (!verificationCode.equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        if (user.getVerificationCodeExpiry() == null || user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new one.");
        }

        user.setEnabled(true);
        user.setVerificationCode(null); // Clear code after successful verification
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);
        return true;
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for this email."));

        if (user.isEnabled()) {
            throw new IllegalArgumentException("Account is already verified.");
        }

        // Generate new verification code and reset expiry
        String newVerificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setVerificationCode(newVerificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), newVerificationCode);
    }
}