package com.glossaar.backend.wordnik.dto;

import java.util.List;

public record WordnikWordDetailsResponse(List<EkiLexeme> lexemes) {

    public record EkiLexeme(String datasetCode, int level1, int level2, EkiMeaning meaning) {}

    public record EkiMeaning(List<DefinitionLangGroup> definitionLangGroups) {}

    public record DefinitionLangGroup(String lang, List<Definition> definitions) {}

    public record Definition(String value) {}
}
