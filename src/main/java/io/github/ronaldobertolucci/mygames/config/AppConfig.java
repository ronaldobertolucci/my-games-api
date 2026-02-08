package io.github.ronaldobertolucci.mygames.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class AppConfig {
    
    @Value("${app.email.from}")
    private String emailFrom;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    @Value("${app.name}")
    private String appName;
    
    @Bean
    public String emailFrom() {
        return emailFrom;
    }
    
    @Bean
    public String frontendUrl() {
        return frontendUrl;
    }
    
    @Bean
    public String appName() {
        return appName;
    }
}