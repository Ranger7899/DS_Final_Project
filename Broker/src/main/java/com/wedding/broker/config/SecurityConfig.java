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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /* 1. Manager area -> Auth0 only */
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
    SecurityFilterChain siteChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/home", "/services/**",
                                "/register", "/images/**", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(f -> f.loginPage("/login").permitAll())              // your login.html
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/"));
        return http.build();
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