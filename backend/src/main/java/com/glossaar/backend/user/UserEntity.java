package com.glossaar.backend.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String displayName;

    protected UserEntity() { } // JPA needs this

    public UserEntity(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }

    public void setUsername(String username) { this.username = username; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
