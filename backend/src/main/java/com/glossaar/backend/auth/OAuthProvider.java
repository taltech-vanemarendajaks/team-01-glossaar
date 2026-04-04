package com.glossaar.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
    GITHUB("GITHUB"),
    GOOGLE("GOOGLE");

    private final String value;
}