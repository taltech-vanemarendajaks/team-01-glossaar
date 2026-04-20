package com.glossaar.backend.word;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.category.CategoryRepository;
import com.glossaar.backend.category.CategoryService;
import com.glossaar.backend.userword.UserWordScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordServiceTest extends IntegrationTest {

    @Autowired
    WordService wordService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    UserWordScoreRepository userWordScoreRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        userWordScoreRepository.deleteAll();
        wordRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getAllByCategoryIds() {
        CategoryEntity food = categoryService.create("Food");

        WordEntity word = new WordEntity();
        word.setWord("Beef");
        word.setCategory(food);
        word.setUser(testUser);
        wordRepository.save(word);

        List<WordEntity> words = wordService.getAllByCategoryIds(List.of(food.getId()));

        assertEquals(1, words.size());
        assertEquals("Beef", words.getFirst().getWord());
    }

    @Test
    void getAllByCategoryIds_noWordsReferenceCategory_returnsEmptyList() {
        CategoryEntity food = categoryService.create("Food");
        CategoryEntity drink = categoryService.create("Drink");

        WordEntity word = new WordEntity();
        word.setWord("Beef");
        word.setCategory(food);
        word.setUser(testUser);
        wordRepository.save(word);

        List<WordEntity> words = wordService.getAllByCategoryIds(List.of(drink.getId()));

        assertEquals(0, words.size());
    }
}
