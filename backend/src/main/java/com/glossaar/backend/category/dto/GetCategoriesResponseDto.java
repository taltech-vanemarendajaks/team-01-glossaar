package com.glossaar.backend.category.dto;

import java.util.List;

public record GetCategoriesResponseDto(
    List<CategoryResponseDto> items,
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
