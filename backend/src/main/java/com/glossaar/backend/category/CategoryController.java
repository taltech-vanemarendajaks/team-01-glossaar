package com.glossaar.backend.category;

import com.glossaar.backend.category.dto.CategoryResponseDto;
import com.glossaar.backend.word.docs.BadRequestApiResponse;
import com.glossaar.backend.word.docs.InternalServerErrorApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Get categories", description = "Returns all categories sored by name.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories fetched successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponseDto.class))
        )
    })
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    public List<CategoryResponseDto> getAll() {
        List<CategoryEntity> result = categoryService.getAll();
        return categoryMapper.toGetCategoriesResponseDto(result);
    }
}
