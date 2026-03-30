package com.glossaar.backend.quiz;

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
    private final UserWordScoreRepository userWordScoreRepository;

    public List<QuizQuestionResponseDto> getQuestionSet(Long userId, int requestedSize) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        int quizSetSize = normalizeQuizSetSize(requestedSize);
        List<WordEntity> quizWords = quizRepository.findLowestScoreQuizWordsByUserId(userId, quizSetSize);
        if (quizWords.size() < quizSetSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least " + quizSetSize + " words with explanations are required for quiz for user: " + userId
            );
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
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least " + OPTIONS_PER_QUESTION + " unique explanations are required in the vocabulary for quiz options"
                );
            }

            Collections.shuffle(options);
            int correctIndex = options.indexOf(question.getExplanation());
            questionSet.add(new QuizQuestionResponseDto(question.getId(), question.getWord(), options, correctIndex));
        }

        return questionSet;
    }

    private static int normalizeQuizSetSize(int requestedSize) {
        if (requestedSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be at least 1");
        }
        if (requestedSize > MAX_QUIZ_SET_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "size must be between 1 and " + MAX_QUIZ_SET_SIZE
            );
        }
        return requestedSize;
    }

    @Transactional
    public QuizSubmitResponseDto submitAnswers(QuizBatchAnswerRequestDto request) {
        if (!userRepository.existsById(request.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.userId());
        }

        if (request.answers().size() > MAX_SUBMIT_BATCH_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "answers must contain at most " + MAX_SUBMIT_BATCH_SIZE + " items"
            );
        }

        Set<Long> seenWordIds = new HashSet<>();
        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            if (!seenWordIds.add(answer.wordId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "answers must not contain duplicate wordId values"
                );
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            int delta = answer.correct() ? 1 : -1;
            int updatedRows = userWordScoreRepository.incrementScoreAndTouch(
                    request.userId(),
                    answer.wordId(),
                    delta,
                    now
            );
            if (updatedRows == 0) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User-word score not found for userId=" + request.userId() + ", wordId=" + answer.wordId()
                );
            }
        }

        return new QuizSubmitResponseDto("ok");
    }
}
