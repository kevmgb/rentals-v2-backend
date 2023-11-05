package com.example.rentalsv2backend.auth.exception;

public class AnonymousException extends RuntimeException {

    private static final String DUPLICATE_EMAIL_MESSAGE = "Email is already registered.";

    public AnonymousException(String message) {
        super(message);
    }

    public static AnonymousException registerDuplicatedEmail() {
        return new AnonymousException(DUPLICATE_EMAIL_MESSAGE);
    }
}
