// src/main/java/com/wedding/broker/service/AuthUserDetailsService.java
package com.wedding.broker.service;

import com.wedding.broker.model.User;
import com.wedding.broker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));


        // IMPORTANT: Check if the user account is enabled
        if (!user.isEnabled()) {
            // If the account is not enabled, throw a DisabledException
            // This will prevent the user from logging in until their account is enabled (e.g., via email verification)
            throw new DisabledException("Account is not enabled. Please verify your email.");
        }

        // No roles, so pass an empty list of authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // No roles/authorities
        );
    }
}