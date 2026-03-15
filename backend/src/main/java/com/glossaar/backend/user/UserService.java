package com.glossaar.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
    }
}
