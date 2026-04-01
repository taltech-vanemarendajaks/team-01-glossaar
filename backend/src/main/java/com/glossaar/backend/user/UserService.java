package com.glossaar.backend.user;

import com.glossaar.backend.user.dto.UserResponseDto;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public List<UserResponseDto> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponseDto getById(Long id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        return toResponse(entity);
    }

    @Transactional
    public UserResponseDto create(String username) {
        String normalizedUsername = requireNonBlank("username", username);

        if (repository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }

        try {
            UserEntity saved = repository.save(
                    new UserEntity(normalizedUsername, emailFromUsername(normalizedUsername)));
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }
    }

    private static String requireNonBlank(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must not be blank");
        }
        return value.trim();
    }

    private static String emailFromUsername(String username) {
        return username.toLowerCase() + "@local.glossaar";
    }

    private UserResponseDto toResponse(UserEntity entity) {
        return new UserResponseDto(entity.getId(), entity.getUsername());
    }

    public UserEntity upsertOAuth2User(OAuth2User oAuth2User) {

        String providerId = oAuth2User.getName();
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = "github";

        return repository.findByAuthProviderAndProviderId(provider, providerId)
                .map(existing -> {
                    log.info("Found an existing account {}", existing.getId());
                    // if we'll need to update some info from provider side, then this can be done
                    // here.

                    return existing;
                })
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();

                    user.setAuthProvider(provider);
                    user.setProviderId(providerId);
                    user.setEmail(email);

                    UserEntity savedUser = repository.save(user);
                    log.info("Created a new account {}", savedUser.getId());

                    return savedUser;
                });
    }
}
