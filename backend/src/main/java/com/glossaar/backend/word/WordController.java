package com.glossaar.backend.word;

import com.glossaar.backend.word.dto.CreateWordRequestDto;
import com.glossaar.backend.word.dto.GetWordsResponseDto;
import com.glossaar.backend.word.dto.UpdateWordRequestDto;
import com.glossaar.backend.word.dto.WordResponseDto;
import com.glossaar.backend.word.docs.BadRequestApiResponse;
import com.glossaar.backend.word.docs.InternalServerErrorApiResponse;
import com.glossaar.backend.word.docs.NotFoundApiResponse;
import com.glossaar.backend.word.mapper.WordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("words")
@RequiredArgsConstructor
public class WordController {

    private final WordService service;
    private final WordMapper mapper;

    @GetMapping
    @Operation(summary = "Get words", description = "Returns paginated words with optional search and sorting.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Words fetched successfully",
                    content = @Content(schema = @Schema(implementation = GetWordsResponseDto.class))
            )
    })
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    public GetWordsResponseDto getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "word") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<WordEntity> result = service.getAll(search, page, size, sortBy, sortDir);
        return mapper.toGetWordsResponseDto(result, search, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get word by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Word fetched successfully",
                    content = @Content(schema = @Schema(implementation = WordResponseDto.class))
            )
    })
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public WordResponseDto getById(@PathVariable Long id) {
        return mapper.toResponseDto(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Word created successfully",
                    content = @Content(schema = @Schema(implementation = WordResponseDto.class))
            )
    })
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    public WordResponseDto create(@Valid @RequestBody CreateWordRequestDto req) {
        return mapper.toResponseDto(service.create(req.word(), req.explanation()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Word updated successfully",
                    content = @Content(schema = @Schema(implementation = WordResponseDto.class))
            )
    })
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public WordResponseDto patch(@PathVariable Long id, @Valid @RequestBody UpdateWordRequestDto req) {
        return mapper.toResponseDto(service.patch(id, req.word(), req.explanation()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete word")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Word deleted successfully",
                    content = @Content
            )
    })
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
