package com.glossaar.backend;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // TODO: use this endpoint to fetch user info. e.g. name and profile
    // image(originating from auth provider) etc
    // this can aslo be user to check authentications status on site initial load,
    // #94
    @GetMapping("/me")
    public Principal getMe(Principal principal) {
        return principal;
    }
}
