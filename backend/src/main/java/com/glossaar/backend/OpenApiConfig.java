package com.glossaar.backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Glossaar Backend API",
                version = "v1",
                description = "REST API for Glossaar backend services.",
                contact = @Contact(name = "Glossaar Team")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local backend direct access"),
                @Server(url = "http://localhost:5173", description = "Frontend dev proxy access")
        }
)
public class OpenApiConfig {
}
