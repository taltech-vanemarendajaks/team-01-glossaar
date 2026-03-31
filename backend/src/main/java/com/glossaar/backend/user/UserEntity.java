package com.glossaar.backend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 255)
    private String username;

    @Column(unique = true, length = 255)
    private String email;

    // TODO: move authProvider and providerId to separate table so there can be
    // multiple auth providers per user
    @Column(nullable = false, length = 255)
    private String authProvider;

    @Column(nullable = false, length = 255)
    private String providerId;

    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
