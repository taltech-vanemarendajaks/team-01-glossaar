package com.glossaar.backend;

import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserEntity> allUsers() {
        return userService.getAll();
    }

    @PostMapping("/users")
    public UserEntity createUser(@RequestBody CreateUserRequest req) {
        return userService.create(req.username(), req.displayName());
    }

    @GetMapping("/users/{username}")
    public UserEntity userByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    public record CreateUserRequest(String username, String displayName) {}
}