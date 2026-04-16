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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EkiServiceTest {

    private MockRestServiceServer mockServer;
    private EkiService ekiService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://ekilex.ee");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        ekiService = new EkiService(builder.build());
    }

    @Test
    void getExplanations() {
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
    }

    @Test
    void getExplanations_throwsBadGateway() {
        mockServer.expect(requestTo("https://ekilex.ee/api/word/search/koer"))
            .andRespond(withServerError());

        assertThatThrownBy(() -> ekiService.getExplanations("koer"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("502")
            .hasMessageContaining("EKI service unavailable");
    }
}
