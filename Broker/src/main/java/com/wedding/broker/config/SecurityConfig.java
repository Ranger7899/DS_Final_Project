package com.wedding.broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
    private final UserDetailsService userDetailsService;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository, UserDetailsService userDetailsService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userDetailsService = userDetailsService;
    }

    // --- Auth0 Security Filter Chain (for /manager/**) ---
    // This chain should be processed first for manager paths.
    @Bean
    @Order(1) // Higher precedence for manager paths
    public SecurityFilterChain managerSecurityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring managerSecurityFilterChain (Auth0) for /manager/** paths.");
        http
                .securityMatcher("/manager/**") // ONLY apply this chain to paths starting with /manager/
                .authorizeHttpRequests(auth -> auth
                        // Ensure manager-specific paths require MANAGER role, others just authenticated
                        .requestMatchers("/manager/orders").hasRole("MANAGER")
                        .anyRequest().authenticated() // All other /manager/** paths require authentication via Auth0
                )
                .oauth2Login(oauth2 -> oauth2
                        // Auth0 will handle the login redirect automatically when an unauthenticated /manager/** path is accessed
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(this.userAuthoritiesMapper())
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .logoutRequestMatcher(new AntPathRequestMatcher("/manager/logout")) // Specific logout for Auth0
                        .permitAll() // Allow logout for /manager/logout
                );
        return http.build();
    }

    // --- Local Form Login Security Filter Chain (for all other paths) ---
    // This chain should be processed after the manager chain.
    @Bean
    @Order(2) // Lower precedence for all other paths
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring userSecurityFilterChain (Local Form Login) for other paths.");
        http
                // This chain implicitly handles all requests NOT matched by the higher-ordered managerSecurityFilterChain
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/services", "/services/**", "/confirm", "/confirm/**",
                                "/photographers", "/photographers/**", "/order", "/images/**", "/css/**", "/js/**",
                                "/login", "/signup", "/register" // These are the public paths for local users
                        ).permitAll() // Publicly accessible paths (no authentication needed)
                        .anyRequest().authenticated() // All other paths (not /manager/** and not permitAll above) require local form login
                )
                .formLogin(form -> form
                        .loginPage("/login") // Your custom local login page
                        .defaultSuccessUrl("/", true) // Redirect to home after successful local login
                        .permitAll() // Allow access to login page itself
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Local logout for general users
                        .logoutSuccessUrl("/login?logout") // Redirect to login page with logout param
                        .permitAll() // Allow logout for /logout
                );
        return http.build();
    }

    // --- Helper Beans ---

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/"); // Redirect to home after Auth0 logout
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

                    if (attributes.containsKey("roles")) {
                        Object rolesAttr = attributes.get("roles");
                        if (rolesAttr instanceof Collection) {
                            ((Collection<?>) rolesAttr).stream()
                                    .filter(String.class::isInstance)
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()))
                                    .forEach(mappedAuthorities::add);
                        }
                    }
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
                    mappedAuthorities.add(oauth2UserAuthority);
                } else {
                    mappedAuthorities.add(authority);
                }
            });
            return mappedAuthorities;
        };
    }

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