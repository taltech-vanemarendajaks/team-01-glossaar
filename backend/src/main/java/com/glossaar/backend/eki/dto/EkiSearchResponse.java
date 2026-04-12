package com.glossaar.backend.eki.dto;

import java.util.List;

public record EkiSearchResponse(List<EkiWord> words) {
    public record EkiWord(long wordId, int homonymNr, String lang) {}
}
