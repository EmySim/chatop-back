package com.rental.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Entity class representing a user in the system.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Default constructor (required by Hibernate).
     */
    public User() {}

    /**
     * Constructor for creating a user with a specific role.
     */
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructor with default role (USER).
     */
    public User(String email, String name, String password) {
        this(email, name, password, Role.USER);
    }

    // Auto-setting timestamps for database persistence
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        logger.info("User created at: " + createdAt);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        logger.info("User updated at: " + updatedAt);
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    // Implementing `UserDetails` methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
