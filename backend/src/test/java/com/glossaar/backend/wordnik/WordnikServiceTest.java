package com.glossaar.backend.wordnik;

import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto.ExplanationGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WordnikServiceTest {

    private MockRestServiceServer mockServer;
    private WordnikService wordnikService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.wordnik.com/v4");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        wordnikService = new WordnikService(builder.build());
        ReflectionTestUtils.setField(wordnikService, "apiKey", "test-key");
    }

    @Test
    void getExplanations_groupsByPartOfSpeech() {
        mockServer.expect(requestTo("https://api.wordnik.com/v4/word.json/hey/definitions?api_key=test-key"))
            .andRespond(withSuccess("""
                [
                  {"partOfSpeech": "interjection", "text": "Used to attract attention."},
                  {"partOfSpeech": "interjection", "text": "Used to express greeting."},
                  {"partOfSpeech": "adjective",    "text": "High."}
                ]""", MediaType.APPLICATION_JSON));

        List<ExplanationGroup> result = wordnikService.getExplanations("hey");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).groupNumber()).isEqualTo(1);
        assertThat(result.get(0).explanations()).containsExactly("Used to attract attention.", "Used to express greeting.");
        assertThat(result.get(1).groupNumber()).isEqualTo(2);
        assertThat(result.get(1).explanations()).containsExactly("High.");
    }

    @Test
    void getExplanations_nullPartOfSpeech_appendedToLastGroup() {
        mockServer.expect(requestTo("https://api.wordnik.com/v4/word.json/hey/definitions?api_key=test-key"))
            .andRespond(withSuccess("""
                [
                  {"partOfSpeech": "interjection", "text": "An exclamation."},
                  {"text": "Archaic form."}
                ]""", MediaType.APPLICATION_JSON));

        List<ExplanationGroup> result = wordnikService.getExplanations("hey");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().explanations()).containsExactly("An exclamation.", "Archaic form.");
    }

    @Test
    void getExplanations_onlyNullPartOfSpeech_singleGroup() {
        mockServer.expect(requestTo("https://api.wordnik.com/v4/word.json/hey/definitions?api_key=test-key"))
            .andRespond(withSuccess("""
                [
                  {"text": "First def."},
                  {"text": "Second def."}
                ]""", MediaType.APPLICATION_JSON));

        List<ExplanationGroup> result = wordnikService.getExplanations("hey");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().groupNumber()).isEqualTo(1);
        assertThat(result.getFirst().explanations()).containsExactly("First def.", "Second def.");
    }

    @Test
    void getExplanations_emptyResponse_returnsEmptyList() {
        mockServer.expect(requestTo("https://api.wordnik.com/v4/word.json/hey/definitions?api_key=test-key"))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<ExplanationGroup> result = wordnikService.getExplanations("hey");

        assertThat(result).isEmpty();
    }

    @Test
    void getExplanations_throwsBadGateway() {
        mockServer.expect(requestTo("https://api.wordnik.com/v4/word.json/hey/definitions?api_key=test-key"))
            .andRespond(withServerError());

        assertThatThrownBy(() -> wordnikService.getExplanations("hey"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("502")
            .hasMessageContaining("Wordnik service unavailable");
    }
}
