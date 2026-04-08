package com.glossaar.backend.category;

import com.glossaar.backend.category.dto.CategoryResponseDto;
import com.glossaar.backend.category.dto.CategoryUpdateDto;
import com.glossaar.backend.word.WordEntity;
import com.glossaar.backend.word.WordService;
import com.glossaar.backend.docs.BadRequestApiResponse;
import com.glossaar.backend.docs.InternalServerErrorApiResponse;
import com.glossaar.backend.docs.NotFoundApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final WordService wordService;

    @GetMapping
    @Operation(summary = "Get categories", description = "Returns all categories sorted by name.")
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
        List<CategoryEntity> categories = categoryService.getAll();

        List<Long> categoryIds = categories.stream().map(CategoryEntity::getId).toList();

        List<WordEntity> wordsForCategories = wordService.getAllByCategoryIds(categoryIds);

        List<CategoryResponseDto> response = new ArrayList<>();
        for (CategoryEntity category : categories) {
            long wordCount = wordsForCategories.stream()
                .filter(word -> word.getCategory().getId().equals(category.getId()))
                .count();
            response.add(categoryMapper.toResponseDto(category, wordCount));
        }

        return response;
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch category", description = "Update category name by ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponseDto.class))
        )
    })
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public void update(@PathVariable Long id, @RequestBody CategoryUpdateDto dto) {
        categoryService.update(id, dto.name());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category", description = "Deletes a category by ID if no words reference it")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Category deleted successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cannot delete category: some words reference it",
            content = @Content(schema = @Schema(hidden = true))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content = @Content(schema = @Schema(hidden = true))
        )
    })
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
