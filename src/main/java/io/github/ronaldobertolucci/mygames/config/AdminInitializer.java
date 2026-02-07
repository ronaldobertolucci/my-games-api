package io.github.ronaldobertolucci.mygames.config;

import io.github.ronaldobertolucci.mygames.model.user.Role;
import io.github.ronaldobertolucci.mygames.model.user.User;
import io.github.ronaldobertolucci.mygames.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.initialization.enabled:false}")
    private boolean adminInitEnabled;
    
    @Value("${admin.email}")
    private String adminEmail;
    
    @Value("${admin.password}")
    private String adminPassword;

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!adminInitEnabled) {
            logger.info("Admin initialization disabled");
            return;
        }

        if (!userRepository.existsByUsername(adminEmail)) {
            User admin = new User();
            admin.setUsername(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            admin.setEnabled(true);
            
            userRepository.save(admin);

            System.out.println("═══════════════════════════════════════");
            System.out.println("Admin user created:");
            System.out.println("Email: " + adminEmail);
            System.out.println("Password: " + adminPassword);
            System.out.println("⚠️  CHANGE THIS PASSWORD IMMEDIATELY!");
            System.out.println("═══════════════════════════════════════");
        }
    }
}