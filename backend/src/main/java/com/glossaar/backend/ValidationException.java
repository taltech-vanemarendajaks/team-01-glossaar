package com.glossaar.backend;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> args;

    public ValidationException(String message) {
        this(message, List.of());
    }

    public ValidationException(String message, List<String> args) {
        super(message);
        this.args = args == null ? List.of() : List.copyOf(args);
    }

    public ValidationException(String message, String... args) {
        this(message, args == null ? List.of() : List.of(args));
    }

    public List<String> getArgs() {
        return args;
    }
}
