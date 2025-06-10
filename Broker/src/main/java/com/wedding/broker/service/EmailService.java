package com.wedding.broker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // NEW
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") // Injects the username from application.properties
    private String fromEmail; // NEW field

    public void sendVerificationEmail(String toEmail, String username, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Use the injected email address
        message.setTo(toEmail);
        message.setSubject("Verify Your Wedding Broker Account");
        message.setText("Dear " + username + ",\n\n"
                + "Thank you for registering with Wedding Broker. To complete your registration, "
                + "please use the following verification code:\n\n"
                + "Verification Code: " + verificationCode + "\n\n"
                + "This code is valid for 15 minutes.\n\n"
                + "If you did not register for this service, please ignore this email.\n\n"
                + "Best regards,\n"
                + "The Wedding Broker Team");
        mailSender.send(message);
    }
}