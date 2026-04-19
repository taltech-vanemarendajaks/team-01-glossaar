package com.glossaar.backend.word;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.word.dto.CreateWordRequestDto;
import com.glossaar.backend.word.dto.GetWordsResponseDto;
import com.glossaar.backend.word.dto.WordResponseDto;
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

        controller.create(request, testUserPrincipal);

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

        controller.create(request, testUserPrincipal);

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
            controller.create(request, testUserPrincipal);
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
            controller.create(request, testUserPrincipal);
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
            controller.create(request, testUserPrincipal);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("category must not be blank");

        List<CategoryEntity> categories = categoryRepository.findAll();
        List<WordEntity> words = wordRepository.findAll();
        assertEquals(0, categories.size());
        assertEquals(0, words.size());
    }

    @Test
    void getAll_includesCategoryNameForEachWord() {
        controller.create(new CreateWordRequestDto("Car", "Vehicle", "Transport"));
        controller.create(new CreateWordRequestDto("Apple", "Fruit", "Food"));

        GetWordsResponseDto response = controller.getAll("", 0, 10, "word", "asc");

        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).word()).isEqualTo("Apple");
        assertThat(response.items().get(0).categoryName()).isEqualTo("Food");
        assertThat(response.items().get(1).word()).isEqualTo("Car");
        assertThat(response.items().get(1).categoryName()).isEqualTo("Transport");
    }

    @Test
    void getAll_withSearch_includesCategoryName() {
        controller.create(new CreateWordRequestDto("Banana", "Yellow", "Food"));
        controller.create(new CreateWordRequestDto("Train", "Rail", "Transport"));

        GetWordsResponseDto response = controller.getAll("Banana", 0, 10, "word", "asc");

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().word()).isEqualTo("Banana");
        assertThat(response.items().getFirst().categoryName()).isEqualTo("Food");
    }

    @Test
    void getAll_wordWithoutCategory_returnsNullCategoryName() {
        wordRepository.save(new WordEntity("Legacy", "Old data without category"));

        GetWordsResponseDto response = controller.getAll("", 0, 10, "word", "asc");

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().word()).isEqualTo("Legacy");
        assertThat(response.items().getFirst().categoryName()).isNull();
    }

    @Test
    void getById_includesCategoryName() {
        WordResponseDto created = controller.create(
                new CreateWordRequestDto("Desk", "Furniture", "Office"));

        WordResponseDto byId = controller.getById(created.id());

        assertThat(byId.id()).isEqualTo(created.id());
        assertThat(byId.word()).isEqualTo("Desk");
        assertThat(byId.explanation()).isEqualTo("Furniture");
        assertThat(byId.categoryName()).isEqualTo("Office");
    }

    @Test
    void getById_wordWithoutCategory_returnsNullCategoryName() {
        WordEntity saved = wordRepository.save(new WordEntity("Orphan", "No category row"));

        WordResponseDto byId = controller.getById(saved.getId());

        assertThat(byId.word()).isEqualTo("Orphan");
        assertThat(byId.categoryName()).isNull();
    }
}
