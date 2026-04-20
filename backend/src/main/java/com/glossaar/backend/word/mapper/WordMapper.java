package com.glossaar.backend.word.mapper;

import com.glossaar.backend.word.WordEntity;
import com.glossaar.backend.word.dto.GetWordsResponseDto;
import com.glossaar.backend.word.dto.WordResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    public WordResponseDto toResponseDto(WordEntity entity) {
        return new WordResponseDto(
            entity.getId(),
            entity.getWord(),
            entity.getExplanation(),
            entity.getCategory() != null
                ? entity.getCategory().getName()
                : null
        );
    }

    public GetWordsResponseDto toGetWordsResponseDto(
            Page<WordEntity> page,
            String search,
            String sortBy,
            String sortDir
    ) {
        return new GetWordsResponseDto(
                page.getContent().stream().map(this::toResponseDto).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious(),
                search == null ? "" : search,
                sortBy == null ? "word" : sortBy,
                sortDir == null ? "asc" : sortDir
        );
    }
}

