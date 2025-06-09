package com.wedding.broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order; // Ensure this is imported
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Ensure this is imported
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserDetailsService userDetailsService; // Inject UserDetailsService

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository, UserDetailsService userDetailsService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userDetailsService = userDetailsService;
    }

    // --- Auth0 Security Filter Chain (for /manager/**) ---
    @Bean
    @Order(1) // This ensures this filter chain is processed first
    public SecurityFilterChain managerSecurityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring managerSecurityFilterChain (Auth0) for /manager/** paths.");
        http
                .securityMatcher("/manager/**") // Apply this chain ONLY to /manager/**
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/manager/orders").hasRole("MANAGER") // Only MANAGER role can access this
                        .anyRequest().authenticated() // All other /manager paths require authentication (via Auth0)
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(this.userAuthoritiesMapper()) // Still map roles from Auth0
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()) // Auth0 specific logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/manager/logout")) // Specify logout for manager path
                        .permitAll()
                );
        return http.build();
    }

    // --- Local Form Login Security Filter Chain (for all other paths) ---
    @Bean
    @Order(2) // This ensures this filter chain is processed second
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring userSecurityFilterChain (Local Form Login) for other paths.");
        http
                // Paths allowed for everyone without authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/services", "/services/**", "/confirm", "/confirm/**",
                                "/photographers", "/photographers/**", "/order", "/images/**", "/css/**", "/js/**",
                                "/login", "/signup", "/register",
                                "/oauth2/authorization/**", // Allow Auth0 login initiation
                                "/login/oauth2/code/**"     // Allow Auth0 callback
                        ).permitAll()
                        .anyRequest().authenticated() // All other paths require authentication (local)
                )
                .formLogin(form -> form
                        .loginPage("/login") // Your custom login page URL
                        .defaultSuccessUrl("/", true) // Redirect after successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Default logout URL for local users
                        .logoutSuccessUrl("/login?logout") // Redirect to login page after local logout
                        .permitAll()
                );
        return http.build();
    }

    // --- Helper Beans (No changes needed here unless specified) ---

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/"); // Redirect to home page after Auth0 logout
        return handler;
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> attributes = oidcUserAuthority.getAttributes();

                    // Check for roles within the 'roles' claim or 'https://your-domain/roles' if customized
                    // Auth0 often puts custom claims in a namespaced format, e.g., "https://<your-domain>/roles"
                    // Adjust this claim name based on your Auth0 configuration
                    if (attributes.containsKey("roles")) { // Or "https://<your-domain>/roles"
                        Object rolesAttr = attributes.get("roles"); // Or "https://<your-domain>/roles"
                        if (rolesAttr instanceof Collection) {
                            ((Collection<?>) rolesAttr).stream()
                                    .filter(String.class::isInstance)
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()))
                                    .forEach(mappedAuthorities::add);
                        }
                    }
                    // Corrected: Add the OidcUserAuthority itself to preserve original authorities
                    mappedAuthorities.add(oidcUserAuthority);
                } else if (authority instanceof OAuth2UserAuthority) {
                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                    Map<String, Object> attributes = oauth2UserAuthority.getAttributes();

                    if (attributes.containsKey("roles")) {
                        Object rolesAttr = attributes.get("roles");
                        if (rolesAttr instanceof Collection) {
                            ((Collection<?>) rolesAttr).stream()
                                    .filter(String.class::isInstance)
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()))
                                    .forEach(mappedAuthorities::add);
                        }
                    }
                    // Corrected: Add the OAuth2UserAuthority itself to preserve original authorities
                    mappedAuthorities.add(oauth2UserAuthority);
                } else {
                    mappedAuthorities.add(authority);
                }
            });
            return mappedAuthorities;
        };
    }

    // For local user authentication
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}