package com.glossaar.backend.word;

import com.glossaar.backend.word.dto.CreateWordRequestDto;
import com.glossaar.backend.word.dto.GetWordsResponseDto;
import com.glossaar.backend.word.dto.UpdateWordRequestDto;
import com.glossaar.backend.word.dto.WordResponseDto;
import com.glossaar.backend.word.mapper.WordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordControllerTest {

    @Mock
    private WordService service;

    @Mock
    private WordMapper mapper;

    private WordController controller;

    @BeforeEach
    void setUp() {
        controller = new WordController(service, mapper);
    }

    @Test
    void getAll_mapsServicePageToResponse() {
        WordEntity word = new WordEntity("alpha", "first");
        Page<WordEntity> page = new PageImpl<>(List.of(word));
        GetWordsResponseDto dto = new GetWordsResponseDto(
                List.of(new WordResponseDto(1L, "alpha", "first")),
                1L, 1, 0, 10, false, false, "alp", "word", "asc"
        );
        when(service.getAll("alp", 0, 10, "word", "asc")).thenReturn(page);
        when(mapper.toGetWordsResponseDto(page, "alp", "word", "asc")).thenReturn(dto);

        GetWordsResponseDto response = controller.getAll("alp", 0, 10, "word", "asc");

        assertEquals(1, response.items().size());
        assertEquals("alpha", response.items().getFirst().word());
        assertEquals("alp", response.search());
        assertEquals("word", response.sortBy());
        assertEquals("asc", response.sortDir());
    }

    @Test
    void create_returnsCreatedWord() {
        WordEntity created = new WordEntity("lorem", "ipsum");
        WordResponseDto dto = new WordResponseDto(1L, "lorem", "ipsum");
        when(service.create("lorem", "ipsum")).thenReturn(created);
        when(mapper.toResponseDto(created)).thenReturn(dto);

        WordResponseDto response = controller.create(new CreateWordRequestDto("lorem", "ipsum"));

        assertEquals("lorem", response.word());
        assertEquals("ipsum", response.explanation());
    }

    @Test
    void patch_returnsUpdatedWord() {
        WordEntity updated = new WordEntity("updated", "changed");
        WordResponseDto dto = new WordResponseDto(5L, "updated", "changed");
        when(service.patch(5L, "updated", "changed")).thenReturn(updated);
        when(mapper.toResponseDto(updated)).thenReturn(dto);

        WordResponseDto response = controller.patch(5L, new UpdateWordRequestDto("updated", "changed"));

        assertEquals("updated", response.word());
        assertEquals("changed", response.explanation());
    }

    @Test
    void delete_callsService() {
        controller.delete(9L);
        verify(service).delete(9L);
    }
}
