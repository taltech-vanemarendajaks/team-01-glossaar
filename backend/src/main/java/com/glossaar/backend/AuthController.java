package com.glossaar.backend;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/")
    public String home() {
        return "Unsecured by design";
    }

    @GetMapping("/me")
    public Principal getMe(Principal principal) {
        return principal;
    }
}
