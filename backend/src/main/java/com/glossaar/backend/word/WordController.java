package com.glossaar.backend.word;

import com.glossaar.backend.ApiExceptionHandler.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService service;

    @GetMapping
    @Operation(summary = "Get words", description = "Returns paginated words with optional search and sorting.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Words fetched successfully",
                    content = @Content(schema = @Schema(implementation = GetWordsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query params",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public GetWordsResponse getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "word") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<WordEntity> result = service.getAll(search, page, size, sortBy, sortDir);
        return new GetWordsResponse(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize(),
                result.hasNext(),
                result.hasPrevious(),
                search == null ? "" : search,
                sortBy == null ? "word" : sortBy,
                sortDir == null ? "asc" : sortDir
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get word by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Word fetched successfully",
                    content = @Content(schema = @Schema(implementation = WordEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Word not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public WordEntity getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Word created successfully",
                    content = @Content(schema = @Schema(implementation = WordEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public WordEntity create(@RequestBody CreateWordRequest req) {
        return service.create(req.word(), req.explanation());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Word updated successfully",
                    content = @Content(schema = @Schema(implementation = WordEntity.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Word not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public WordEntity patch(@PathVariable Long id, @RequestBody UpdateWordRequest req) {
        return service.patch(id, req.word(), req.explanation());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Word deleted successfully",
                    content = @Content(schema = @Schema(implementation = DeleteWordResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Word not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public DeleteWordResponse delete(@PathVariable Long id) {
        service.delete(id);
        return new DeleteWordResponse("Word deleted successfully", id);
    }

    public record CreateWordRequest(String word, String explanation) {
    }

    public record UpdateWordRequest(String word, String explanation) {
    }

    public record DeleteWordResponse(String message, Long id) {
    }

    public record GetWordsResponse(
            List<WordEntity> items,
            long totalItems,
            int totalPages,
            int page,
            int size,
            boolean hasNext,
            boolean hasPrevious,
            String search,
            String sortBy,
            String sortDir
    ) {
    }
}
