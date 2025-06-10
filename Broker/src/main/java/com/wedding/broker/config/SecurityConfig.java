package com.wedding.broker.config;

import com.wedding.broker.model.User;
import com.wedding.broker.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException; // Import this
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository; // Field for clientRegistrationRepository
    private final UserRepository userRepository; // Inject UserRepository to use in custom UserDetailsService


    // ADDED CONSTRUCTOR for injecting dependencies
    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository,
                          UserRepository userRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userRepository = userRepository;
    }

    /* 1. Manager area -> Auth0 only */
    @Bean @Order(1)
    SecurityFilterChain managerChain(HttpSecurity http) throws Exception {
        http
                // IMPORTANT: include both the START and the CALLBACK paths
                .securityMatcher("/manager/**", "/oauth2/**", "/login/oauth2/**")

                .authorizeHttpRequests(a -> a
                        // let Spring hit these URLs without authentication
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().hasRole("MANAGER"))

                .oauth2Login(o -> o
                        .loginPage("/oauth2/authorization/auth0"))     // entry-point for this chain

        /* remove this line if you’re not using JWTs in your own APIs */
        //.oauth2ResourceServer(o -> o.jwt())
        ;
        return http.build();
    }



    /* 2. Everyone else -> form login */
    @Bean @Order(2)
    SecurityFilterChain siteChain(HttpSecurity http,
                                  LogoutSuccessHandler oidcAndLocalLogoutSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/home", "/confirm","/error","/confirm/**","/complete","/order","/order/**","/services/**",
                                "/register", "/images/**","/verify-email","/verify-email/**" ,"/css/**", "/js/**",
                                "/login**").permitAll() // Keep this to ensure /login with params is accessible
                        .anyRequest().authenticated())
                .formLogin(f -> f.loginPage("/login").permitAll().failureHandler(authenticationFailureHandler()))
                .logout(l -> l
                        .logoutUrl("/logout")                  // <a href="/logout"> in home.html
                        .logoutSuccessHandler(oidcAndLocalLogoutSuccessHandler)
                        .invalidateHttpSession(true)           // kill JSESSIONID
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }

    @Bean
    LogoutSuccessHandler oidcAndLocalLogoutSuccessHandler(
            ClientRegistrationRepository registrations) {

        // Handler that knows how to build the Auth0 /v2/logout URL
        OidcClientInitiatedLogoutSuccessHandler oidc =
                new OidcClientInitiatedLogoutSuccessHandler(registrations);
        // Where Auth0 should send the user afterwards
        oidc.setPostLogoutRedirectUri("{baseUrl}/");

        // Wrap it so we can decide at runtime
        return (request, response, authentication) -> {
            if (authentication != null
                    && authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
                // Manager → full Auth0 logout
                oidc.onLogoutSuccess(request, response, authentication);
            } else {
                // Regular visitor → just kill the local session
                new org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler() {{
                    setDefaultTargetUrl("/");
                }}.onLogoutSuccess(request, response, authentication);
            }
        };
    }

    // Custom AuthenticationFailureHandler to provide specific messages
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                String errorMessage = "Login failed. Please check your username and password."; // Default generic message

                // Check the cause of the exception for more specific messages
                Throwable cause = exception.getCause();

                if (cause instanceof UsernameNotFoundException) {
                    errorMessage = "No account found with that username.";
                } else if (cause instanceof DisabledException) {
                    errorMessage = "Account is not enabled. Please verify your email.";
                } else if (exception instanceof BadCredentialsException) {
                    // This catches general bad credentials if no more specific cause is found
                    errorMessage = "Invalid username or password.";
                }
                // You can add more specific messages for other exceptions if needed
                // e.g., else if (cause instanceof LockedException) { ... }

                // Redirect back to login page with the specific error message as a query parameter
                super.setDefaultFailureUrl("/login?error=" + errorMessage);
                super.onAuthenticationFailure(request, response, exception);
            }
        };
    }


    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        final String customRolesClaim = "https://weddingbroker.com/claims/roles";

        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> claims = oidcUserAuthority.getAttributes();

                    if (claims.containsKey(customRolesClaim)) {
                        Object rolesObject = claims.get(customRolesClaim);
                        if (rolesObject instanceof Collection) {
                            Collection<String> roles = (Collection<String>) rolesObject;
                            roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}