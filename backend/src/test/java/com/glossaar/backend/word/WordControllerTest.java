package com.glossaar.backend.word;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.word.dto.CreateWordRequestDto;
import com.glossaar.backend.category.CategoryRepository;
import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WordControllerTest extends IntegrationTest {

    @Autowired
    private WordController controller;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserWordScoreRepository userWordScoreRepository;

    @BeforeEach
    void setup() {
        userWordScoreRepository.deleteAll();
        wordRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void create_wordWithCategory() {
        assertEquals(0, wordRepository.count());
        assertEquals(0, categoryRepository.count());

        CreateWordRequestDto request = new CreateWordRequestDto(
            "Word",
            "Word explanation",
            "Word category"
        );

        controller.create(request);

        List<WordEntity> words = wordRepository.findAll();
        List<CategoryEntity> categories = categoryRepository.findAll();

        assertEquals(1, words.size());
        assertEquals(1, categories.size());

        WordEntity savedWord = words.getFirst();
        CategoryEntity savedCategory = categories.getFirst();

        assertEquals("Word", savedWord.getWord());
        assertEquals("Word explanation", savedWord.getExplanation());

        assertNotNull(savedWord.getCategory());
        assertEquals(savedCategory.getId(), savedWord.getCategory().getId());
        assertEquals("Word category", savedWord.getCategory().getName());
    }

    @Test
    void create_creatingWordWithExistingCategoryDoesNotDuplicateCategory() {
        CategoryEntity existing = categoryRepository.save(new CategoryEntity("Food"));

        CreateWordRequestDto request = new CreateWordRequestDto(
            "Chicken",
            "Well done",
            "Food"
        );

        controller.create(request);

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<WordEntity> words = wordRepository.findAll();
        assertEquals(1, categories.size());
        assertEquals(1, words.size());

        WordEntity savedWord = words.getFirst();
        assertEquals("Chicken", savedWord.getWord());
        assertEquals(existing.getId(), savedWord.getCategory().getId());
    }

    @Test
    void create_wordIsEmpty_throws() {
        CreateWordRequestDto request = new CreateWordRequestDto(
            "",
            "Foo",
            "Bar"
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.create(request);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("word must not be blank");

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<WordEntity> words = wordRepository.findAll();
        assertEquals(0, categories.size());
        assertEquals(0, words.size());
    }

    @Test
    void create_explanationIsEmpty_throws() {
        CreateWordRequestDto request = new CreateWordRequestDto(
            "Foo",
            "    ",
            "Bar"
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.create(request);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("explanation must not be blank");

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<WordEntity> words = wordRepository.findAll();
        assertEquals(0, categories.size());
        assertEquals(0, words.size());
    }
    @Test
    void create_categoryIsEmpty_throws() {
        CreateWordRequestDto request = new CreateWordRequestDto(
            "Foo",
            "Bar",
            "   "
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.create(request);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("category must not be blank");

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<WordEntity> words = wordRepository.findAll();
        assertEquals(0, categories.size());
        assertEquals(0, words.size());
    }
}
