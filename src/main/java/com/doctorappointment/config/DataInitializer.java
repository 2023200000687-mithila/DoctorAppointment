package com.doctorappointment.config;

import com.doctorappointment.model.User;
import com.doctorappointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_NAME:System Administrator}")
    private String adminName;

    @Value("${ADMIN_EMAIL:admin@gmail.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:Admin@12345}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        String email = adminEmail.trim().toLowerCase();
        User admin = userRepository.findByUsernameIgnoreCase(email).orElse(null);

        if (admin == null) {
            admin = User.builder()
                    .username(email)
                    .role("ADMIN")
                    .enabled(true)
                    .build();
            System.out.println("Default admin account created for: " + adminName + " (" + email + ")");
        }

        // Keep the configured demo admin credentials usable even if an older
        // admin document already exists in MongoDB.
        admin.setUsername(email);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        userRepository.save(admin);
    }
}
