package com.glossaar.backend.word;

import com.glossaar.backend.word.dto.GetWordsResponseDto;
import com.glossaar.backend.word.dto.WordResponseDto;
import com.glossaar.backend.word.mapper.WordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordMapperTest {

    private final WordMapper mapper = new WordMapper();

    @Test
    void toResponseDto_mapsEntityFields() {
        WordEntity entity = new WordEntity("lorem", "ipsum");

        WordResponseDto dto = mapper.toResponseDto(entity);

        assertEquals("lorem", dto.word());
        assertEquals("ipsum", dto.explanation());
    }

    @Test
    void toGetWordsResponseDto_mapsPaginationMetadataAndItems() {
        WordEntity entity = new WordEntity("alpha", "first");
        Page<WordEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        GetWordsResponseDto dto = mapper.toGetWordsResponseDto(page, "alp", "word", "asc");

        assertEquals(1, dto.items().size());
        assertEquals("alpha", dto.items().getFirst().word());
        assertEquals(1L, dto.totalItems());
        assertEquals(1, dto.totalPages());
        assertEquals("alp", dto.search());
        assertEquals("word", dto.sortBy());
        assertEquals("asc", dto.sortDir());
    }
}

