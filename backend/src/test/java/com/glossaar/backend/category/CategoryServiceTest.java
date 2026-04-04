package com.glossaar.backend.category;

import com.glossaar.backend.IntegrationTest;
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
    private CategoryService service;

    @Test
    void getAll_returnsAllCategories_sortedByNameIgnoringCase() {
        CategoryEntity first = service.create("Tech");
        CategoryEntity second = service.create("gardening");
        CategoryEntity third = service.create("Cooking");

        assertEquals(List.of(third, second, first), service.getAll());
    }

    @Test
    void getById_returnsActualCategory() {
        CategoryEntity first = service.create("Tech");
        CategoryEntity second = service.create("Gardening");
        CategoryEntity third = service.create("Cooking");

        assertEquals(third, service.getById(third.getId()));
    }

    @Test
    void getById_throwsWhenNotFound() {
        service.create("Tech");
        service.create("Gardening");
        service.create("Cooking");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.getById(-2L);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Category not found");
    }

    @Test
    void create_savesCategoryWithUpperCase() {
        CategoryEntity cat = service.create("tech");
        assertThat(cat.getId()).isNotNull();
        assertThat(cat.getName()).isEqualTo("Tech");
    }

    @Test
    void create_reusesExistingCategoryIfPresent() {
        CategoryEntity first = service.create("Tech");
        CategoryEntity second = service.create("Tech");

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void create_throwsWhenBlankName() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.create("      ");
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("name must not be blank");
    }
}
