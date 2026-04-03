package com.glossaar.backend.category;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.category.dto.CategoryResponseDto;
import com.glossaar.backend.category.dto.CategoryUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CategoryControllerTest extends IntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    CategoryController controller;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getAll_returnsAllCategories() {
        CategoryEntity first = categoryService.create("Tech");
        CategoryEntity second = categoryService.create("Gardening");
        CategoryEntity third = categoryService.create("ABC");
        CategoryEntity fourth = categoryService.create("Cooking");

        CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto(third.getId(), third.getName());
        CategoryResponseDto categoryResponseDto2 = new CategoryResponseDto(fourth.getId(), fourth.getName());
        CategoryResponseDto categoryResponseDto3 = new CategoryResponseDto(second.getId(), second.getName());
        CategoryResponseDto categoryResponseDto4 = new CategoryResponseDto(first.getId(), first.getName());

        List<CategoryResponseDto> expectedResult = List.of(categoryResponseDto1,
                                                           categoryResponseDto2,
                                                           categoryResponseDto3,
                                                           categoryResponseDto4);
        assertEquals(expectedResult, controller.getAll());
    }

    @Test
    void getAll_returnsEmptyListIfNoCategoryFound() {
        assertEquals(List.of(), controller.getAll());
    }

    @Test
    void update_success(){
        CategoryEntity existingCategory = categoryService.create("Cooking");
        List<CategoryEntity> allCategoriesBeforeUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesBeforeUpdate.size());
        assertEquals(existingCategory, allCategoriesBeforeUpdate.get(0));

        controller.update(existingCategory.getId(), new CategoryUpdateDto("New name"));

        List<CategoryEntity> allCategoriesAfterUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesAfterUpdate.size());
        CategoryEntity updatedCategory = existingCategory;
        updatedCategory.setName("New name");
        assertEquals(updatedCategory, allCategoriesAfterUpdate.getFirst());
    }

    @Test
    void update_nameIsEmpty(){
        CategoryEntity existingCategory = categoryService.create("Cooking");
        List<CategoryEntity> allCategoriesBeforeUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesBeforeUpdate.size());
        assertEquals(existingCategory, allCategoriesBeforeUpdate.get(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.update(existingCategory.getId(), new CategoryUpdateDto("   "));
        });

        assertEquals("Category name cannot be empty", exception.getMessage());

        List<CategoryEntity> allCategoriesAfterUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesAfterUpdate.size());
        assertEquals(existingCategory, allCategoriesAfterUpdate.getFirst());
    }
}
