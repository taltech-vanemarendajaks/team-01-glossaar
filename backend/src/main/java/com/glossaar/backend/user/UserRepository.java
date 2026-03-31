package com.glossaar.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    // will face conflicts if the same user want to use multiple providers.
    // (duplicate account for same user) Not currently a problem in our case
    Optional<UserEntity> findByAuthProviderAndProviderId(String authProvider, String providerId);
}
