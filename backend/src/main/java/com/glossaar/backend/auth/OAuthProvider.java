package com.glossaar.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
    GITHUB("GITHUB"),
    GOOGLE("GOOGLE");

    private final String value;

    public static OAuthProvider fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return OAuthProvider.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}