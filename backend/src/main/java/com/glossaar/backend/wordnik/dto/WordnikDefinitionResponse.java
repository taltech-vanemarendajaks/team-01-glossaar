package com.glossaar.backend.wordnik.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WordnikDefinitionResponse(String partOfSpeech, String text) {}
