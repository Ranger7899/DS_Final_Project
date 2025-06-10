// src/main/java/com/wedding/broker/model/User.java
package com.wedding.broker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime; // Import LocalDateTime

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean enabled;

    private String verificationCode;

    private LocalDateTime verificationCodeExpiry; // New field for verification code expiry

    // Default constructor is required by JPA
    public User() {
    }

    // Constructor for creating new User objects (without ID, enabled, verificationCode, verificationCodeExpiry initially)
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = false; // By default, user is not enabled until email is verified
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiry() { // New getter
        return verificationCodeExpiry;
    }

    public void setVerificationCodeExpiry(LocalDateTime verificationCodeExpiry) { // New setter
        this.verificationCodeExpiry = verificationCodeExpiry;
    }
}