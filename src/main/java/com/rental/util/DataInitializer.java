package com.rental.util;

import com.rental.entity.User;
import com.rental.entity.Role;
import com.rental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer is a component that initializes default users if the database is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin@email.com", "Admin Name", passwordEncoder.encode("adminpass"), Role.ADMIN));
            userRepository.save(new User("user@email.com", "User Name", passwordEncoder.encode("userpass"), Role.USER));
            System.out.println("✅ Utilisateurs injectés !");
        }
    }
}
