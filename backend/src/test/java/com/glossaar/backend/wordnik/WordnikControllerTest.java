package com.glossaar.backend.wordnik;

import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto;
import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto.ExplanationGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordnikControllerTest {

    @Mock
    private WordnikService wordnikService;

    @InjectMocks
    private WordnikController wordnikController;

    @Test
    void getExplanations() {
        when(wordnikService.getExplanations(any())).thenReturn(List.of(
            new ExplanationGroup(1, List.of("Used to attract attention.", "Used to express greeting.")),
            new ExplanationGroup(2, List.of("High."))
        ));

        WordnikExplanationsResponseDto result = wordnikController.getExplanations("hey");

        assertThat(result.word()).isEqualTo("hey");
        assertThat(result.explanationGroups()).hasSize(2);
        assertThat(result.explanationGroups().get(0).explanations()).containsExactly("Used to attract attention.", "Used to express greeting.");
        assertThat(result.explanationGroups().get(1).explanations()).containsExactly("High.");
    }

    @Test
    void getExplanations_noResults_returnsEmptyList() {
        when(wordnikService.getExplanations(any())).thenReturn(List.of());

        WordnikExplanationsResponseDto result = wordnikController.getExplanations("xyz");

        assertThat(result.word()).isEqualTo("xyz");
        assertThat(result.explanationGroups()).isEmpty();
    }
}
