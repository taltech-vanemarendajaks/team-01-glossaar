package com.glossaar.backend.quiz;

import com.glossaar.backend.quiz.dto.QuizBatchAnswerRequestDto;
import com.glossaar.backend.quiz.dto.QuizQuestionResponseDto;
import com.glossaar.backend.quiz.dto.QuizSubmitResponseDto;
import com.glossaar.backend.user.UserRepository;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.userword.UserWordScoreEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    private static final long USER_ID = 1L;
    private static final long WORD_A_ID = 101L;
    private static final long WORD_B_ID = 102L;
    private static final long WORD_C_ID = 103L;
    private static final long WORD_D_ID = 104L;
    private static final long WORD_E_ID = 105L;
    private static final long WORD_F_ID = 106L;
    private static final long WORD_G_ID = 107L;
    private static final long WORD_H_ID = 108L;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserWordScoreRepository userWordScoreRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getQuestionSetReturnsFourQuestionsEachWithFourOptionsAndValidCorrectIndex() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(quizRepository.findLowestScoreQuizWordsByUserId(USER_ID, 8)).thenReturn(List.of(
                word(WORD_A_ID, "Word A", "Explanation A"),
                word(WORD_B_ID, "Word B", "Explanation B"),
                word(WORD_C_ID, "Word C", "Explanation C"),
                word(WORD_D_ID, "Word D", "Explanation D"),
                word(WORD_E_ID, "Word E", "Explanation E"),
                word(WORD_F_ID, "Word F", "Explanation F"),
                word(WORD_G_ID, "Word G", "Explanation G"),
                word(WORD_H_ID, "Word H", "Explanation H")
        ));

        List<QuizQuestionResponseDto> result = quizService.getQuestionSet(USER_ID);

        assertThat(result).hasSize(4);
        for (QuizQuestionResponseDto question : result) {
            assertThat(question.wordId()).isNotNull();
            assertThat(question.word()).isNotBlank();
            assertThat(question.options()).hasSize(4);
            assertThat(question.correctIndex()).isBetween(0, 3);
            assertThat(question.options().get(question.correctIndex())).isNotBlank();
        }
    }

    @Test
    void getQuestionSetFailsWhenThereAreFewerThanEightCandidates() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(quizRepository.findLowestScoreQuizWordsByUserId(USER_ID, 8)).thenReturn(List.of(
                word(WORD_A_ID, "Word A", "Explanation A"),
                word(WORD_B_ID, "Word B", "Explanation B"),
                word(WORD_C_ID, "Word C", "Explanation C"),
                word(WORD_D_ID, "Word D", "Explanation D"),
                word(WORD_E_ID, "Word E", "Explanation E"),
                word(WORD_F_ID, "Word F", "Explanation F"),
                word(WORD_G_ID, "Word G", "Explanation G")
        ));

        assertThatThrownBy(() -> quizService.getQuestionSet(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("At least 8 words with explanations are required for quiz for user: " + USER_ID);
    }

    @Test
    void getQuestionSetFailsWhenThereAreNotEnoughUniqueExplanations() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(quizRepository.findLowestScoreQuizWordsByUserId(USER_ID, 8)).thenReturn(List.of(
                word(WORD_A_ID, "Word A", "Same explanation"),
                word(WORD_B_ID, "Word B", "Same explanation"),
                word(WORD_C_ID, "Word C", "Same explanation"),
                word(WORD_D_ID, "Word D", "Same explanation"),
                word(WORD_E_ID, "Word E", "Same explanation"),
                word(WORD_F_ID, "Word F", "Same explanation"),
                word(WORD_G_ID, "Word G", "Same explanation"),
                word(WORD_H_ID, "Word H", "Same explanation")
        ));

        assertThatThrownBy(() -> quizService.getQuestionSet(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("At least 4 unique explanations are required for quiz");
    }

    @Test
    void getQuestionSetSucceedsWhenPoolHasDuplicatesButEnoughUniqueExplanations() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(quizRepository.findLowestScoreQuizWordsByUserId(USER_ID, 8)).thenReturn(List.of(
                word(WORD_A_ID, "Word A", "Explanation A"),
                word(WORD_B_ID, "Word B", "Explanation B"),
                word(WORD_C_ID, "Word C", "Explanation C"),
                word(WORD_D_ID, "Word D", "Explanation D"),
                word(WORD_E_ID, "Word E", "Explanation A"),
                word(WORD_F_ID, "Word F", "Explanation B"),
                word(WORD_G_ID, "Word G", "Explanation C"),
                word(WORD_H_ID, "Word H", "Explanation D")
        ));

        List<QuizQuestionResponseDto> result = quizService.getQuestionSet(USER_ID);

        assertThat(result).hasSize(4);
        for (QuizQuestionResponseDto question : result) {
            assertThat(question.options()).hasSize(4);
        }
    }

    @Test
    void getQuestionSetFailsWhenUserDoesNotExist() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> quizService.getQuestionSet(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found: " + USER_ID);
    }

    @Test
    void submitAnswersUpdatesAllFiveAndReturnsOk() {
        UserEntity user = new UserEntity("TestUser");
        user.setId(USER_ID);
        UserWordScoreEntity scoreA = new UserWordScoreEntity(user, word(WORD_A_ID, "Word A", "Explanation A"), 3);
        UserWordScoreEntity scoreB = new UserWordScoreEntity(user, word(WORD_B_ID, "Word B", "Explanation B"), 2);
        UserWordScoreEntity scoreC = new UserWordScoreEntity(user, word(WORD_C_ID, "Word C", "Explanation C"), 1);
        UserWordScoreEntity scoreD = new UserWordScoreEntity(user, word(WORD_D_ID, "Word D", "Explanation D"), 0);
        UserWordScoreEntity scoreE = new UserWordScoreEntity(user, word(WORD_E_ID, "Word E", "Explanation E"), -1);

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_A_ID)).thenReturn(java.util.Optional.of(scoreA));
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_B_ID)).thenReturn(java.util.Optional.of(scoreB));
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_C_ID)).thenReturn(java.util.Optional.of(scoreC));
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_D_ID)).thenReturn(java.util.Optional.of(scoreD));
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_E_ID)).thenReturn(java.util.Optional.of(scoreE));

        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(
                USER_ID,
                List.of(
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_A_ID, true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_B_ID, false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_C_ID, true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_D_ID, false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_E_ID, true)
                )
        );

        QuizSubmitResponseDto response = quizService.submitAnswers(request);

        assertThat(response.message()).isEqualTo("ok");
        assertThat(scoreA.getQuizScore()).isEqualTo(4);
        assertThat(scoreB.getQuizScore()).isEqualTo(1);
        assertThat(scoreC.getQuizScore()).isEqualTo(2);
        assertThat(scoreD.getQuizScore()).isEqualTo(-1);
        assertThat(scoreE.getQuizScore()).isEqualTo(0);
        verify(userWordScoreRepository, times(5)).save(org.mockito.ArgumentMatchers.any(UserWordScoreEntity.class));
    }

    @Test
    void submitAnswersFailsWhenUserWordScoreRowDoesNotExist() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userWordScoreRepository.findByUserIdAndWordId(USER_ID, WORD_A_ID)).thenReturn(java.util.Optional.empty());

        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(
                USER_ID,
                List.of(
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_A_ID, true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_B_ID, false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_C_ID, true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_D_ID, false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_E_ID, true)
                )
        );

        assertThatThrownBy(() -> quizService.submitAnswers(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User-word score not found for userId=" + USER_ID + ", wordId=" + WORD_A_ID);
    }

    @Test
    void submitAnswersFailsWhenDuplicateWordIdsProvided() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(
                USER_ID,
                List.of(
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_A_ID, true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto(WORD_A_ID, false)
                )
        );

        assertThatThrownBy(() -> quizService.submitAnswers(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("answers must not contain duplicate wordId values");
    }

    @Test
    void submitAnswersFailsWhenBatchSizeExceedsMaxLimit() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        List<QuizBatchAnswerRequestDto.QuizAnswerItemDto> answers = new ArrayList<>();
        IntStream.rangeClosed(1, 101).forEach(i ->
                answers.add(new QuizBatchAnswerRequestDto.QuizAnswerItemDto((long) i, true))
        );
        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(USER_ID, answers);

        assertThatThrownBy(() -> quizService.submitAnswers(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("answers must contain at most 100 items");
    }

    private static WordEntity word(Long id, String word, String explanation) {
        WordEntity entity = new WordEntity(word, explanation);
        entity.setId(id);
        return entity;
    }
}
