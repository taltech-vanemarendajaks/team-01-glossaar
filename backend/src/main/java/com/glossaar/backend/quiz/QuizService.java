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
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final int QUIZ_SET_SIZE = 4;
    private static final int CANDIDATE_POOL_MULTIPLIER = 2;
    private static final int CANDIDATE_POOL_SIZE = QUIZ_SET_SIZE * CANDIDATE_POOL_MULTIPLIER;
    private static final int OPTIONS_PER_QUESTION = 4;

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
        List<WordEntity> quizWords = new ArrayList<>(candidateWords.subList(0, QUIZ_SET_SIZE));

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
            questionSet.add(new QuizQuestionResponseDto(question.getWord(), options, correctIndex));
        }

        return questionSet;
    }

    @Transactional
    public QuizSubmitResponseDto submitAnswers(QuizBatchAnswerRequestDto request) {
        if (!userRepository.existsById(request.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.userId());
        }

        for (QuizBatchAnswerRequestDto.QuizAnswerItemDto answer : request.answers()) {
            UserWordScoreEntity userWordScore = userWordScoreRepository
                    .findByUserIdAndWordWordIgnoreCase(request.userId(), answer.word())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User-word score not found for userId=" + request.userId() + ", word=" + answer.word()
                    ));

            int delta = answer.correct() ? 1 : -1;
            userWordScore.setQuizScore(userWordScore.getQuizScore() + delta);
            userWordScoreRepository.save(userWordScore);
        }

        return new QuizSubmitResponseDto("ok");
    }
}
