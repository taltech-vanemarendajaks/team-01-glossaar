package com.glossaar.backend.category;

import com.glossaar.backend.category.dto.CategoryResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {
    public CategoryResponseDto toResponseDto(CategoryEntity entity) {
        return new CategoryResponseDto(entity.getId(), entity.getName());
    }

    public List<CategoryResponseDto> toGetCategoriesResponseDto(List<CategoryEntity> categories) {
        return categories.stream().map(this::toResponseDto).toList();
    }
}
