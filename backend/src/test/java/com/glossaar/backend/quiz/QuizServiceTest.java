package com.glossaar.backend.quiz;

import com.glossaar.backend.IntegrationTest;
import com.glossaar.backend.category.CategoryEntity;
import com.glossaar.backend.category.CategoryRepository;
import com.glossaar.backend.eki.EkiWordEntity;
import com.glossaar.backend.eki.EkiWordRepository;
import com.glossaar.backend.quiz.dto.QuizQuestionResponseDto;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.user.UserRepository;
import com.glossaar.backend.userword.UserWordScoreEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordEntity;
import com.glossaar.backend.word.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuizServiceTest extends IntegrationTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private UserWordScoreRepository userWordScoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EkiWordRepository ekiWordRepository;

    @BeforeEach
    void setup() {
        userWordScoreRepository.deleteAll();
        wordRepository.deleteAll();
        categoryRepository.deleteAll();
        ekiWordRepository.deleteAll();
    }

    @Test
    void getQuestionSet_withCategoryOwnedByAnotherUser_returnsNotFound() {
        UserEntity otherUser = userRepository.save(new UserEntity("other-user", "other@example.com"));
        CategoryEntity otherCategory = categoryRepository.save(new CategoryEntity("Other category", otherUser));

        WordEntity otherUserWord = new WordEntity("private-word", "private explanation", otherUser);
        otherUserWord.setCategory(otherCategory);
        otherUserWord = wordRepository.save(otherUserWord);
        userWordScoreRepository.save(new UserWordScoreEntity(otherUser, otherUserWord, 0));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> quizService.getQuestionSet(testUser.getId(), 1, otherCategory.getId())
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains("Category not found");
    }

    @Test
    void getQuestionSet_usesEkiWordsTableForDistractors() {
        WordEntity questionWord = wordRepository.save(new WordEntity("kass", "correct-expl", testUser));
        userWordScoreRepository.save(new UserWordScoreEntity(testUser, questionWord, 0));

        ekiWordRepository.save(new EkiWordEntity("koer", "koer", "est", 1, "wrong-1"));
        ekiWordRepository.save(new EkiWordEntity("hunt", "hunt", "est", 1, "wrong-2"));
        ekiWordRepository.save(new EkiWordEntity("rebane", "rebane", "est", 1, "wrong-3"));

        QuizQuestionResponseDto question = quizService.getQuestionSet(testUser.getId(), 1, null).getFirst();

        assertThat(question.word()).isEqualTo("kass");
        assertThat(question.options()).hasSize(4);
        assertThat(question.options()).contains("correct-expl", "wrong-1", "wrong-2", "wrong-3");
    }

    @Test
    void getQuestionSet_whenEkiDistractorsInsufficient_returnsBadRequest() {
        WordEntity questionWord = wordRepository.save(new WordEntity("kass", "correct-expl", testUser));
        userWordScoreRepository.save(new UserWordScoreEntity(testUser, questionWord, 0));

        ekiWordRepository.save(new EkiWordEntity("koer", "koer", "est", 1, "wrong-1"));
        ekiWordRepository.save(new EkiWordEntity("hunt", "hunt", "est", 1, "wrong-2"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> quizService.getQuestionSet(testUser.getId(), 1, null)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("EKI cache");
    }
}
