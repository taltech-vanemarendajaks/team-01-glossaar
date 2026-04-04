package com.glossaar.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
    GITHUB("GITHUB");
    // TODO: add more providers as needed

    private final String value;
}