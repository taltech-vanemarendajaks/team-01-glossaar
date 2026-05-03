package com.glossaar.backend.category;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.ValidationException;
import com.glossaar.backend.category.dto.CategoryResponseDto;
import com.glossaar.backend.category.dto.CategoryUpdateDto;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordEntity;
import com.glossaar.backend.word.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class CategoryControllerTest extends IntegrationTest {

    @Autowired
    UserWordScoreRepository userWordScoreRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    CategoryController controller;

    @BeforeEach
    void setup() {
        userWordScoreRepository.deleteAll();
        wordRepository.deleteAll();
        categoryRepository.deleteAll();
    }
    @Test
    void getAll_returnsAllCategories() {
        CategoryEntity first = categoryService.create("Tech", testUser);
        first.setUser(testUser);
        CategoryEntity second = categoryService.create("Gardening", testUser);
        second.setUser(testUser);
        CategoryEntity third = categoryService.create("ABC", testUser);
        third.setUser(testUser);
        CategoryEntity fourth = categoryService.create("Cooking", testUser);
        fourth.setUser(testUser);

        CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto(third.getId(), third.getName(), 0L);
        CategoryResponseDto categoryResponseDto2 = new CategoryResponseDto(fourth.getId(), fourth.getName(), 0L);
        CategoryResponseDto categoryResponseDto3 = new CategoryResponseDto(second.getId(), second.getName(), 0L);
        CategoryResponseDto categoryResponseDto4 = new CategoryResponseDto(first.getId(), first.getName(), 0L);

        List<CategoryResponseDto> expectedResult = List.of(categoryResponseDto1,
                                                           categoryResponseDto2,
                                                           categoryResponseDto3,
                                                           categoryResponseDto4);
        assertEquals(expectedResult, controller.getAll(testUserPrincipal));
    }

    @Test
    void getAll_returnsEmptyListIfNoCategoryFound() {
        assertEquals(List.of(), controller.getAll(testUserPrincipal));
    }

    @Test
    void update_success(){
        CategoryEntity existingCategory = categoryService.create("Cooking", testUser);
        List<CategoryEntity> allCategoriesBeforeUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesBeforeUpdate.size());
        assertEquals(existingCategory, allCategoriesBeforeUpdate.get(0));

        controller.update(existingCategory.getId(), new CategoryUpdateDto("New name"),testUserPrincipal);

        List<CategoryEntity> allCategoriesAfterUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesAfterUpdate.size());
        CategoryEntity updatedCategory = existingCategory;
        updatedCategory.setName("New name");
        assertEquals(updatedCategory, allCategoriesAfterUpdate.getFirst());
    }

    @Test
    void update_nameIsEmpty(){
        CategoryEntity existingCategory = categoryService.create("Cooking", testUser);
        List<CategoryEntity> allCategoriesBeforeUpdate = categoryRepository.findAllByUserOrdered(testUser);
        assertEquals(1, allCategoriesBeforeUpdate.size());
        assertEquals(existingCategory, allCategoriesBeforeUpdate.get(0));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.update(existingCategory.getId(), new CategoryUpdateDto("   "), testUserPrincipal);
        });

        assertEquals("field: blank", exception.getMessage());
        assertThat(exception.getArgs()).containsExactly("name");

        List<CategoryEntity> allCategoriesAfterUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesAfterUpdate.size());
        assertEquals(existingCategory, allCategoriesAfterUpdate.getFirst());
    }

    @Test
    void delete(){
        CategoryEntity existingCategory = categoryService.create("Cooking", testUser);
        List<CategoryEntity> allCategoriesBeforeUpdate = categoryRepository.findAll();
        assertEquals(1, allCategoriesBeforeUpdate.size());
        assertEquals(existingCategory, allCategoriesBeforeUpdate.get(0));

        controller.delete(existingCategory.getId(), testUserPrincipal);

        List<CategoryEntity> allCategoriesAfterUpdate = categoryRepository.findAll();
        assertEquals(0, allCategoriesAfterUpdate.size());
    }

    @Test
    void delete_wordIsUsingCategory_canNotBeDeleted(){
        CategoryEntity food = categoryService.create("Food", testUser);
        List<CategoryEntity> allCategoriesBeforeDelete = categoryRepository.findAll();
        assertEquals(1, allCategoriesBeforeDelete.size());
        assertEquals(food, allCategoriesBeforeDelete.getFirst());

        WordEntity word = new WordEntity("Beef", "Red meat", testUser);
        word.setCategory(food);
        wordRepository.save(word);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.delete(food.getId(), testUserPrincipal);
        });

        assertThat(exception.getMessage()).isEqualTo("category: hasWords");
        assertThat(exception.getArgs()).containsExactly("1");

        List<CategoryEntity> allCategoriesAfterDelete = categoryRepository.findAll();
        assertEquals(1, allCategoriesAfterDelete.size());
        assertEquals(food, allCategoriesAfterDelete.getFirst());
    }
}
