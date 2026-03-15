package com.glossaar.backend.word.dto;

import java.util.List;

public record GetWordsResponseDto(
        List<WordResponseDto> items,
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

