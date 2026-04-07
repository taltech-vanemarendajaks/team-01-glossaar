package com.glossaar.backend.category;

import com.glossaar.backend.category.dto.CategoryResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDto toResponseDto(CategoryEntity entity, long wordCount) {
        return new CategoryResponseDto(entity.getId(), entity.getName(), wordCount);
    }
}
