package com.glossaar.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    // TODO: add multi auth support. Email will be the factor that determines who
    // the user is across auth providers, #92
    Optional<UserEntity> findByAuthProviderAndProviderId(String authProvider, String providerId);
}
