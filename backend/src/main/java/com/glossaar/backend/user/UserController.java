package com.glossaar.backend.user;

import com.glossaar.backend.user.dto.CreateUserRequestDto;
import com.glossaar.backend.user.dto.UserResponseDto;
import com.glossaar.backend.word.docs.BadRequestApiResponse;
import com.glossaar.backend.word.docs.InternalServerErrorApiResponse;
import com.glossaar.backend.word.docs.NotFoundApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints.")
public class UserController {

    private final UserService service;

    @GetMapping
    @Operation(summary = "Get users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))
            )
    })
    @InternalServerErrorApiResponse
    public List<UserResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            )
    })
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public UserResponseDto getById(
            @Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Payload for creating a user.",
            content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
    )
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    public UserResponseDto create(@Valid @RequestBody CreateUserRequestDto req) {
        return service.create(req.username());
    }
}
