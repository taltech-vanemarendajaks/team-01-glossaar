package com.glossaar.backend.auth;

import com.glossaar.backend.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_auth_providers", uniqueConstraints = @UniqueConstraint(columnNames = { "provider_name",
        "provider_user_id" }))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "provider_name", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private OAuthProvider providerName;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    public OAuthAccount(UserEntity user, OAuthProvider providerName, String providerUserId) {
        this.user = user;
        this.providerName = providerName;
        this.providerUserId = providerUserId;
    }
}
