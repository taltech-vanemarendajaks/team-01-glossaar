package com.glossaar.backend.eki;

import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto;
import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto.ExplanationGroup;
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
class EkiControllerTest {

    @Mock
    private EkiService ekiService;

    @InjectMocks
    private EkiController ekiController;

    @Test
    void getExplanations() {
        when(ekiService.getExplanations(any())).thenReturn(List.of(
            new ExplanationGroup(1, List.of("kest", "rasvakiht")),
            new ExplanationGroup(2, List.of("lauljad"))
        ));

        EkiExplanationsResponseDto result = ekiController.getExplanations("koor");

        assertThat(result.word()).isEqualTo("koor");
        assertThat(result.explanationGroups()).hasSize(2);
        assertThat(result.explanationGroups().get(0).explanations()).containsExactly("kest", "rasvakiht");
        assertThat(result.explanationGroups().get(1).explanations()).containsExactly("lauljad");
    }

    @Test
    void getExplanations_noResults_returnsEmptyList() {
        when(ekiService.getExplanations(any())).thenReturn(List.of());

        EkiExplanationsResponseDto result = ekiController.getExplanations("xyz");

        assertThat(result.word()).isEqualTo("xyz");
        assertThat(result.explanationGroups()).isEmpty();
    }
}
