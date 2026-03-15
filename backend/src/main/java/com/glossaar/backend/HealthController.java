package com.glossaar.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "Working Test";
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/api/user")
    public String user() {
        return "I am Robot";
    }
}