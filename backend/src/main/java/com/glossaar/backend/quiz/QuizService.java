package com.glossaar.backend.quiz;

import com.glossaar.backend.ValidationException;
import com.glossaar.backend.category.CategoryRepository;
import com.glossaar.backend.quiz.dto.QuizBatchAnswerRequestDto;
import com.glossaar.backend.quiz.dto.QuizQuestionResponseDto;
import com.glossaar.backend.quiz.dto.QuizSubmitResponseDto;
import com.glossaar.backend.user.UserRepository;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final int MAX_QUIZ_SET_SIZE = 50;
    private static final int OPTIONS_PER_QUESTION = 4;
    private static final int MAX_SUBMIT_BATCH_SIZE = 100;

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserWordScoreRepository userWordScoreRepository;

    public List<QuizQuestionResponseDto> getQuestionSet(Long userId, int requestedSize, Long categoryId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryId);
        }

        int quizSetSize = normalizeQuizSetSize(requestedSize);
        List<WordEntity> quizWords = categoryId == null
                ? quizRepository.findLowestScoreQuizWordsByUserId(userId, quizSetSize)
                : quizRepository.findLowestScoreQuizWordsByUserIdAndCategoryId(userId, categoryId, quizSetSize);
        if (quizWords.size() < quizSetSize) {
            throw new ValidationException("quiz: notEnoughWords", String.valueOf(quizSetSize));
        }

        List<QuizQuestionResponseDto> questionSet = new ArrayList<>(quizSetSize);

        for (WordEntity question : quizWords) {
            List<String> options = new ArrayList<>(OPTIONS_PER_QUESTION);
            options.add(question.getExplanation());

            List<String> distractors = quizRepository.findRandomDistractorExplanations(
                    question.getId(),
                    question.getExplanation(),
                    OPTIONS_PER_QUESTION - 1
            );
            options.addAll(distractors);

            if (options.size() < OPTIONS_PER_QUESTION) {
                throw new ValidationException("quiz: notEnoughDistractors", String.valueOf(OPTIONS_PER_QUESTION));
            }

            Collections.shuffle(options);
            int correctIndex = options.indexOf(question.getExplanation());
            questionSet.add(new QuizQuestionResponseDto(question.getId(), question.getWord(), options, correctIndex));
        }

        return questionSet;
    }

    private static int normalizeQuizSetSize(int requestedSize) {
        if (requestedSize < 1) {
            throw new ValidationException("size: outOfRange", "1", String.valueOf(MAX_QUIZ_SET_SIZE));
        }
        if (requestedSize > MAX_QUIZ_SET_SIZE) {
            throw new ValidationException("size: outOfRange", "1", String.valueOf(MAX_QUIZ_SET_SIZE));
        }
        return requestedSize;
    }

    @Transactional
    public QuizSubmitResponseDto submitAnswers(Long userId, QuizBatchAnswerRequestDto request) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        if (request.answers().size() > MAX_SUBMIT_BATCH_SIZE) {
            throw new ValidationException("answers: tooMany", String.valueOf(MAX_SUBMIT_BATCH_SIZE));
        }

        Set<Long> seenWordIds = new HashSet<>();
        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            if (!seenWordIds.add(answer.wordId())) {
                throw new ValidationException("answers: duplicateWordId");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            int delta = answer.correct() ? 1 : -1;
            int updatedRows = userWordScoreRepository.incrementScoreAndTouch(
                    userId,
                    answer.wordId(),
                    delta,
                    now
            );
            if (updatedRows == 0) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User-word score not found for userId=" + userId + ", wordId=" + answer.wordId()
                );
            }
        }

        return new QuizSubmitResponseDto("ok");
    }
}
