package com.glossaar.backend.wordnik.dto;

import java.util.List;

public record WordnikSearchResponse(List<EkiWord> words) {
    public record EkiWord(long wordId, int homonymNr, String lang) {}
}
