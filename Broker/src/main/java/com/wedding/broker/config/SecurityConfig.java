// src/main/java/com/wedding/broker/config/SecurityConfig.java
package com.wedding.broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.wedding.broker.service.AuthUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Keep if you might add method security later
public class SecurityConfig {

    private final AuthUserDetailsService authUserDetailsService;

    public SecurityConfig(AuthUserDetailsService authUserDetailsService) {
        this.authUserDetailsService = authUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/services", "/services/**", "/confirm", "/confirm/**", "/photographers", "/photographers/**", "/order", "/images/**", "/css/**", "/js/**", "/register", "/login").permitAll() // Publicly accessible pages
                        .requestMatchers("/manager/orders").hasRole("MANAGER") // Requires MANAGER role - KEEP THIS
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .defaultSuccessUrl("/home", true) // Redirect to home on successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Redirect to home on successful logout
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/oauth2/authorization/auth0") // If using Auth0 for external login
                        // You can re-add GrantedAuthoritiesMapper here if you use OAuth2 and need to map external roles
                );
        // .csrf().disable(); // Consider enabling CSRF for production and handling it in forms
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}