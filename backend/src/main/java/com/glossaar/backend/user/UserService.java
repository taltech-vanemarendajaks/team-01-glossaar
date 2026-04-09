package com.glossaar.backend.user;

import com.glossaar.backend.auth.OAuthAccount;
import com.glossaar.backend.auth.OAuthAccountRepository;
import com.glossaar.backend.auth.OAuthProvider;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final OAuthAccountRepository oauthAccountRepository;

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

    @Transactional(readOnly = true)
    public UserEntity getByIdEntity(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
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

    @Transactional
    public UserEntity upsertOAuth2User(OAuth2User oAuth2User, OAuthProvider provider) {
        String providerId = oAuth2User.getName(); // get oauth provider user id or 'name', e.g. github user id

        String avatarUrl = getAvatarUrl(oAuth2User, provider).orElse(null);

        // TODO: currently each provider will create a new account. We'll need to find a
        // a constant between them that we can use to link the accounts. (email was not
        // returned in case of github so we can't use that)
        return oauthAccountRepository
            .findByProviderNameAndProviderUserId(provider, providerId)
            .map(oauthAccount -> {
                oauthAccount.setAvatarUrl(avatarUrl);
                return oauthAccount.getUser();
            })
            .orElseGet(() -> {
                log.info("Did not find a user with provider:" + provider + ", providerId:" + providerId);
                UserEntity newUser = new UserEntity();

                OAuthAccount account = new OAuthAccount(newUser, provider, providerId);
                account.setAvatarUrl(avatarUrl);

                newUser.addOAuthAccount(account);

                UserEntity savedUser = repository.save(newUser);
                log.info("Created new user account id=" + savedUser.getId() + " with provider:" + provider
                         + ", providerId:" + providerId);

                return savedUser;
            });
    }

    public Optional<OAuthAccount> getOAuthAccount(Long userId) {
        return oauthAccountRepository.findFirstByUserId(userId);
    }

    public UserResponseDto getByOAuthAccount(OAuthProvider provider, String providerUserId) {
        Optional<UserEntity> authEntry = oauthAccountRepository
                .findByProviderNameAndProviderUserId(provider, providerUserId).map(it -> it.getUser());

        if (authEntry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found for provider: " + provider + ", providerUserId: " +
                            providerUserId);
        }

        return toResponse(authEntry.get());
    }

    private Optional<String> getAvatarUrl(OAuth2User user, OAuthProvider provider) {
        var attrs = user.getAttributes();

        return switch (provider) {
            case GOOGLE -> Optional.ofNullable((String) attrs.get("picture"));
            case GITHUB -> Optional.ofNullable((String) attrs.get("avatar_url"));
            default -> Optional.empty();
        };
    }
}
