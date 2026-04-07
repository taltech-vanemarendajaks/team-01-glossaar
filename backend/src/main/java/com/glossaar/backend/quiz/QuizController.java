package com.glossaar.backend.quiz;

import com.glossaar.backend.quiz.dto.QuizBatchAnswerRequestDto;
import com.glossaar.backend.quiz.dto.QuizQuestionResponseDto;
import com.glossaar.backend.quiz.dto.QuizSubmitResponseDto;
import com.glossaar.backend.docs.BadRequestApiResponse;
import com.glossaar.backend.docs.InternalServerErrorApiResponse;
import com.glossaar.backend.docs.NotFoundApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Quiz generation and answer submission endpoints.")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(
            summary = "Get quiz set",
            description = "Returns quiz items for the user. Use query param 'size' to control the number of questions (default: 1, min: 1, max: 50). Optionally provide 'categoryId' to generate questions from one category only. Questions prioritize words never quizzed before, then the least recently quizzed words, with score used as a tie-breaker. Wrong options are sampled from the full vocabulary."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quiz question fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuizQuestionResponseDto.class)))
            )
    })
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public List<QuizQuestionResponseDto> getQuestions(
            @Parameter(
                    description = "Existing user ID for whom the quiz is generated.",
                    example = "1",
                    required = true
            )
            @RequestParam Long userId,
            @Parameter(
                    description = "How many quiz questions to return.",
                    schema = @Schema(type = "integer", defaultValue = "1", minimum = "1", maximum = "50"),
                    example = "1"
            )
            @RequestParam(defaultValue = "1") int size,
            @Parameter(
                    description = "Optional category id to restrict quiz words to one category.",
                    example = "1",
                    schema = @Schema(type = "integer", nullable = true)
            )
            @RequestParam(required = false) Long categoryId
    ) {
        return quizService.getQuestionSet(userId, size, categoryId);
    }

    @PostMapping
    @Operation(
            summary = "Submit quiz answers",
            description = "Updates user quiz scores from one or more answer items (by wordId), sets last_quizzed_at for answered words, and returns success status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quiz scores updated successfully",
                    content = @Content(schema = @Schema(implementation = QuizSubmitResponseDto.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Batch of one or more submitted answers for one user. Each answer references wordId.",
            content = @Content(schema = @Schema(implementation = QuizBatchAnswerRequestDto.class))
    )
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    public QuizSubmitResponseDto submitAnswers(@Valid @RequestBody QuizBatchAnswerRequestDto request) {
        return quizService.submitAnswers(request);
    }
}
