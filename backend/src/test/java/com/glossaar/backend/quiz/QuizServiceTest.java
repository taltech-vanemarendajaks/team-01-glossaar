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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    private static final long USER_ID = 1L;

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
                new WordEntity("Word A", "Explanation A"),
                new WordEntity("Word B", "Explanation B"),
                new WordEntity("Word C", "Explanation C"),
                new WordEntity("Word D", "Explanation D"),
                new WordEntity("Word E", "Explanation E"),
                new WordEntity("Word F", "Explanation F"),
                new WordEntity("Word G", "Explanation G"),
                new WordEntity("Word H", "Explanation H")
        ));

        List<QuizQuestionResponseDto> result = quizService.getQuestionSet(USER_ID);

        assertThat(result).hasSize(4);
        for (QuizQuestionResponseDto question : result) {
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
                new WordEntity("Word A", "Explanation A"),
                new WordEntity("Word B", "Explanation B"),
                new WordEntity("Word C", "Explanation C"),
                new WordEntity("Word D", "Explanation D"),
                new WordEntity("Word E", "Explanation E"),
                new WordEntity("Word F", "Explanation F"),
                new WordEntity("Word G", "Explanation G")
        ));

        assertThatThrownBy(() -> quizService.getQuestionSet(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("At least 8 words with explanations are required for quiz for user: " + USER_ID);
    }

    @Test
    void getQuestionSetFailsWhenThereAreNotEnoughUniqueExplanations() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(quizRepository.findLowestScoreQuizWordsByUserId(USER_ID, 8)).thenReturn(List.of(
                new WordEntity("Word A", "Same explanation"),
                new WordEntity("Word B", "Same explanation"),
                new WordEntity("Word C", "Same explanation"),
                new WordEntity("Word D", "Same explanation"),
                new WordEntity("Word E", "Same explanation"),
                new WordEntity("Word F", "Same explanation"),
                new WordEntity("Word G", "Same explanation"),
                new WordEntity("Word H", "Same explanation")
        ));

        assertThatThrownBy(() -> quizService.getQuestionSet(USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("At least 4 unique explanations are required for quiz");
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
        UserWordScoreEntity scoreA = new UserWordScoreEntity(user, new WordEntity("Word A", "Explanation A"), 3);
        UserWordScoreEntity scoreB = new UserWordScoreEntity(user, new WordEntity("Word B", "Explanation B"), 2);
        UserWordScoreEntity scoreC = new UserWordScoreEntity(user, new WordEntity("Word C", "Explanation C"), 1);
        UserWordScoreEntity scoreD = new UserWordScoreEntity(user, new WordEntity("Word D", "Explanation D"), 0);
        UserWordScoreEntity scoreE = new UserWordScoreEntity(user, new WordEntity("Word E", "Explanation E"), -1);

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word A")).thenReturn(java.util.Optional.of(scoreA));
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word B")).thenReturn(java.util.Optional.of(scoreB));
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word C")).thenReturn(java.util.Optional.of(scoreC));
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word D")).thenReturn(java.util.Optional.of(scoreD));
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word E")).thenReturn(java.util.Optional.of(scoreE));

        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(
                USER_ID,
                List.of(
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word A", true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word B", false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word C", true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word D", false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word E", true)
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
        when(userWordScoreRepository.findByUserIdAndWordWordIgnoreCase(USER_ID, "Word X")).thenReturn(java.util.Optional.empty());

        QuizBatchAnswerRequestDto request = new QuizBatchAnswerRequestDto(
                USER_ID,
                List.of(
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word X", true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word A", false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word B", true),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word C", false),
                        new QuizBatchAnswerRequestDto.QuizAnswerItemDto("Word D", true)
                )
        );

        assertThatThrownBy(() -> quizService.submitAnswers(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User-word score not found for userId=" + USER_ID + ", word=Word X");
    }
}
