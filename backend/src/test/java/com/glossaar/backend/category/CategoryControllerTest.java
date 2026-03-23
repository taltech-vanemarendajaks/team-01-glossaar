package com.glossaar.backend.category;

import com.glossaar.backend.category.dto.CategoryResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
class CategoryControllerTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    CategoryController controller;

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
}
