package com.glossaar.backend.word;

import com.glossaar.backend.word.WordController.CreateWordRequest;
import com.glossaar.backend.word.WordController.DeleteWordResponse;
import com.glossaar.backend.word.WordController.GetWordsResponse;
import com.glossaar.backend.word.WordController.UpdateWordRequest;
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

    private WordController controller;

    @BeforeEach
    void setUp() {
        controller = new WordController(service);
    }

    @Test
    void getAll_mapsServicePageToResponse() {
        WordEntity word = new WordEntity("alpha", "first");
        Page<WordEntity> page = new PageImpl<>(List.of(word));
        when(service.getAll("alp", 0, 10, "word", "asc")).thenReturn(page);

        GetWordsResponse response = controller.getAll("alp", 0, 10, "word", "asc");

        assertEquals(1, response.items().size());
        assertEquals("alpha", response.items().getFirst().getWord());
        assertEquals("alp", response.search());
        assertEquals("word", response.sortBy());
        assertEquals("asc", response.sortDir());
    }

    @Test
    void create_returnsCreatedWord() {
        WordEntity created = new WordEntity("lorem", "ipsum");
        when(service.create("lorem", "ipsum")).thenReturn(created);

        WordEntity response = controller.create(new CreateWordRequest("lorem", "ipsum"));

        assertEquals("lorem", response.getWord());
        assertEquals("ipsum", response.getExplanation());
    }

    @Test
    void patch_returnsUpdatedWord() {
        WordEntity updated = new WordEntity("updated", "changed");
        when(service.patch(5L, "updated", "changed")).thenReturn(updated);

        WordEntity response = controller.patch(5L, new UpdateWordRequest("updated", "changed"));

        assertEquals("updated", response.getWord());
        assertEquals("changed", response.getExplanation());
    }

    @Test
    void delete_returnsSuccessMessage() {
        DeleteWordResponse response = controller.delete(9L);

        assertEquals("Word deleted successfully", response.message());
        assertEquals(9L, response.id());
        verify(service).delete(9L);
    }
}
