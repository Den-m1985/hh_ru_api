package com.example.exceptions;

public class CompanyCreationException extends RuntimeException {
    public CompanyCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
