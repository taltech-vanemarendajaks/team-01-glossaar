package com.glossaar.backend.quiz;

import com.glossaar.backend.quiz.dto.QuizBatchAnswerRequestDto;
import com.glossaar.backend.quiz.dto.QuizQuestionResponseDto;
import com.glossaar.backend.quiz.dto.QuizSubmitResponseDto;
import com.glossaar.backend.user.UserRepository;
import com.glossaar.backend.userword.UserWordScoreEntity;
import com.glossaar.backend.userword.UserWordScoreRepository;
import com.glossaar.backend.word.WordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final int QUIZ_SET_SIZE = 4;
    private static final int CANDIDATE_POOL_MULTIPLIER = 2;
    private static final int CANDIDATE_POOL_SIZE = QUIZ_SET_SIZE * CANDIDATE_POOL_MULTIPLIER;
    private static final int OPTIONS_PER_QUESTION = 4;
    private static final int MAX_SUBMIT_BATCH_SIZE = 100;

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final UserWordScoreRepository userWordScoreRepository;

    public List<QuizQuestionResponseDto> getQuestionSet(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        List<WordEntity> candidateWords = quizRepository.findLowestScoreQuizWordsByUserId(userId, CANDIDATE_POOL_SIZE);
        if (candidateWords.size() < CANDIDATE_POOL_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least " + CANDIDATE_POOL_SIZE + " words with explanations are required for quiz for user: " + userId
            );
        }

        Collections.shuffle(candidateWords);
        List<WordEntity> quizWords = new ArrayList<>(QUIZ_SET_SIZE);
        Set<String> usedExplanations = new HashSet<>();
        for (WordEntity candidate : candidateWords) {
            if (usedExplanations.add(candidate.getExplanation())) {
                quizWords.add(candidate);
            }
            if (quizWords.size() == QUIZ_SET_SIZE) {
                break;
            }
        }

        if (quizWords.size() < QUIZ_SET_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least " + QUIZ_SET_SIZE + " unique explanations are required for quiz"
            );
        }

        List<QuizQuestionResponseDto> questionSet = new ArrayList<>(QUIZ_SET_SIZE);

        for (WordEntity question : quizWords) {
            List<String> options = new ArrayList<>(OPTIONS_PER_QUESTION);
            options.add(question.getExplanation());

            List<String> distractors = new ArrayList<>();
            for (WordEntity candidate : quizWords) {
                if (candidate.getWord().equalsIgnoreCase(question.getWord())) {
                    continue;
                }
                String explanation = candidate.getExplanation();
                if (!options.contains(explanation) && !distractors.contains(explanation)) {
                    distractors.add(explanation);
                }
            }

            Collections.shuffle(distractors);
            for (String distractor : distractors) {
                if (options.size() >= OPTIONS_PER_QUESTION) {
                    break;
                }
                options.add(distractor);
            }

            if (options.size() < OPTIONS_PER_QUESTION) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least " + OPTIONS_PER_QUESTION + " unique explanations are required for quiz"
                );
            }

            Collections.shuffle(options);
            int correctIndex = options.indexOf(question.getExplanation());
            questionSet.add(new QuizQuestionResponseDto(question.getId(), question.getWord(), options, correctIndex));
        }

        return questionSet;
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

        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            UserWordScoreEntity userWordScore = userWordScoreRepository
                    .findByUserIdAndWordId(request.userId(), answer.wordId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User-word score not found for userId=" + request.userId() + ", wordId=" + answer.wordId()
                    ));

            int delta = answer.correct() ? 1 : -1;
            userWordScore.setQuizScore(userWordScore.getQuizScore() + delta);
            userWordScoreRepository.save(userWordScore);
        }

        return new QuizSubmitResponseDto("ok");
    }
}
