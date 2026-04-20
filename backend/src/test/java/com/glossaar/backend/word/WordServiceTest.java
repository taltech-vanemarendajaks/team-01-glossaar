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
        CategoryEntity food = categoryService.create("Food", testUser);

        WordEntity word = new WordEntity();
        word.setWord("Beef");
        word.setCategory(food);
        word.setUser(testUser);
        wordRepository.save(word);

        List<WordEntity> words = wordService.getAllByCategoryIds(List.of(food.getId()), testUser);

        assertEquals(1, words.size());
        assertEquals("Beef", words.getFirst().getWord());
    }

    @Test
    void getAllByCategoryIds_noWordsReferenceCategory_returnsEmptyList() {
        CategoryEntity food = categoryService.create("Food", testUser);
        CategoryEntity drink = categoryService.create("Drink", testUser);

        WordEntity word = new WordEntity();
        word.setWord("Beef");
        word.setCategory(food);
        word.setUser(testUser);
        wordRepository.save(word);

        List<WordEntity> words = wordService.getAllByCategoryIds(List.of(drink.getId()), testUser);

        assertEquals(0, words.size());
    }
}
