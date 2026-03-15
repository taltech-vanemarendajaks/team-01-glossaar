package com.glossaar.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public List<UserEntity> getAll() {
        return repo.findAll();
    }

    public UserEntity create(String username, String displayName) {
        return repo.save(new UserEntity(username, displayName));
    }

    public UserEntity getByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
