package com.glossaar.backend.category;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.ValidationException;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryServiceTest extends IntegrationTest {

    @Autowired
    UserWordScoreRepository userWordScoreRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    CategoryService service;

    @BeforeEach
    void setup() {
        userWordScoreRepository.deleteAll();
        wordRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getAll_returnsAllCategories_sortedByNameIgnoringCase() {
        CategoryEntity first = service.create("Tech", testUser);
        CategoryEntity second = service.create("gardening", testUser);
        CategoryEntity third = service.create("Cooking", testUser);

        assertEquals(List.of(third, second, first), service.getAll(testUser));
    }

    @Test
    void getById_returnsActualCategory() {
        CategoryEntity first = service.create("Tech", testUser);
        CategoryEntity second = service.create("Gardening", testUser);
        CategoryEntity third = service.create("Cooking", testUser);

        assertEquals(third, service.getById(third.getId(), testUser));
    }

    @Test
    void getById_throwsWhenNotFound() {
        service.create("Tech", testUser);
        service.create("Gardening", testUser);
        service.create("Cooking", testUser);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.getById(-1L, testUser);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Category not found");
    }

    @Test
    void create_savesCategoryWithUpperCase() {
        CategoryEntity cat = service.create("tech", testUser);
        assertThat(cat.getId()).isNotNull();
        assertThat(cat.getName()).isEqualTo("Tech");
    }

    @Test
    void create_reusesExistingCategoryIfPresent() {
        CategoryEntity first = service.create("Tech", testUser);
        CategoryEntity second = service.create("Tech", testUser);

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void create_throwsWhenBlankName() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            service.create("      ", testUser);
        });

        assertThat(exception.getMessage()).isEqualTo("field: blank");
        assertThat(exception.getArgs()).containsExactly("name");
    }
}
