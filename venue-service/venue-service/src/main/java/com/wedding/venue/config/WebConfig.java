package com.wedding.venue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://broker.purplemushroom-69ff1346.francecentral.azurecontainerapps.io") // frontend port
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}