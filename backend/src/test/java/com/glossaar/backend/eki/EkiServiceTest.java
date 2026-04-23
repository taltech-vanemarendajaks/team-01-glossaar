package com.glossaar.backend.eki;

import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto.ExplanationGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EkiServiceTest {

    private MockRestServiceServer mockServer;
    private EkiService ekiService;
    private EkiWordRepository ekiWordRepository;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://ekilex.ee");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        ekiWordRepository = mock(EkiWordRepository.class);
        ekiService = new EkiService(builder.build(), ekiWordRepository);
    }

    @Test
    void getExplanations_dbMiss_fetchesFromEkiAndSavesCache() {
        when(ekiWordRepository.findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(eq("koer"), eq("est")))
            .thenReturn(List.of());

        mockServer.expect(requestTo("https://ekilex.ee/api/word/search/koer"))
            .andRespond(withSuccess("""
                                        {"words": [{"wordId": 1, "homonymNr": 1, "lang": "est"}]}""",
                                    MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://ekilex.ee/api/word/details/1"))
            .andRespond(withSuccess("""
                                        {"lexemes": [
                                            {"datasetCode": "eki", "level1": 1, "level2": 1,
                                             "meaning": {"definitionLangGroups": [
                                                {"lang": "est", "definitions": [{"value": "koduloom"}]}
                                             ]}},
                                            {"datasetCode": "eki", "level1": 1, "level2": 2,
                                             "meaning": {"definitionLangGroups": [
                                                {"lang": "est", "definitions": [{"value": "duplicate of level1"}]}
                                             ]}},
                                            {"datasetCode": "eki", "level1": 2, "level2": 1,
                                             "meaning": {"definitionLangGroups": [
                                                {"lang": "est", "definitions": [{"value": "ulakas"}]}
                                             ]}},
                                            {"datasetCode": "other", "level1": 3, "level2": 1,
                                             "meaning": {"definitionLangGroups": [
                                                {"lang": "est", "definitions": [{"value": "non-eki dataset"}]}
                                             ]}}
                                        ]}""",
                                    MediaType.APPLICATION_JSON));

        List<ExplanationGroup> result = ekiService.getExplanations("koer");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().explanations()).isEqualTo(List.of("koduloom", "ulakas"));
        verify(ekiWordRepository).saveAll(anyList());
    }

    @Test
    void getExplanations_dbHit_returnsCachedResultWithoutEkiCall() {
        EkiWordEntity first = new EkiWordEntity();
        first.setWord("koer");
        first.setWordNormalized("koer");
        first.setLang("est");
        first.setHomonymNr(1);
        first.setExplanation("koduloom");

        EkiWordEntity second = new EkiWordEntity();
        second.setWord("koer");
        second.setWordNormalized("koer");
        second.setLang("est");
        second.setHomonymNr(1);
        second.setExplanation("ulakas");

        when(ekiWordRepository.findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(eq("koer"), eq("est")))
            .thenReturn(List.of(first, second));

        List<ExplanationGroup> result = ekiService.getExplanations("koer");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().groupNumber()).isEqualTo(1);
        assertThat(result.getFirst().explanations()).containsExactly("koduloom", "ulakas");
        verify(ekiWordRepository, never()).saveAll(anyList());
    }

    @Test
    void getExplanations_secondRequestForSameWord_readsFromCache() {
        EkiWordEntity cached = new EkiWordEntity();
        cached.setWord("koer");
        cached.setWordNormalized("koer");
        cached.setLang("est");
        cached.setHomonymNr(1);
        cached.setExplanation("koduloom");

        when(ekiWordRepository.findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(eq("koer"), eq("est")))
            .thenReturn(List.of(), List.of(cached));

        mockServer.expect(requestTo("https://ekilex.ee/api/word/search/koer"))
            .andRespond(withSuccess("""
                                        {"words": [{"wordId": 1, "homonymNr": 1, "lang": "est"}]}""",
                                    MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://ekilex.ee/api/word/details/1"))
            .andRespond(withSuccess("""
                                        {"lexemes": [
                                            {"datasetCode": "eki", "level1": 1, "level2": 1,
                                             "meaning": {"definitionLangGroups": [
                                                {"lang": "est", "definitions": [{"value": "koduloom"}]}
                                             ]}}
                                        ]}""",
                                    MediaType.APPLICATION_JSON));

        List<ExplanationGroup> first = ekiService.getExplanations("koer");
        List<ExplanationGroup> second = ekiService.getExplanations("koer");

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
        assertThat(second.getFirst().explanations()).containsExactly("koduloom");
    }

    @Test
    void getExplanations_throwsBadGateway() {
        when(ekiWordRepository.findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(eq("koer"), eq("est")))
            .thenReturn(List.of());

        mockServer.expect(requestTo("https://ekilex.ee/api/word/search/koer"))
            .andRespond(withServerError());

        assertThatThrownBy(() -> ekiService.getExplanations("koer"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("502")
            .hasMessageContaining("EKI service unavailable");
        verify(ekiWordRepository, never()).saveAll(anyList());
    }
}
