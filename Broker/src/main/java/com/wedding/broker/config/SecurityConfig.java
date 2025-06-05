package com.wedding.broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/services/**", "/order", "/images/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(userAuthoritiesMapper())
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
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
}