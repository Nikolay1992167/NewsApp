package com.solbeg.newsservice.exception;

public class CustomServerException extends RuntimeException {

    public CustomServerException(String message) {
        super(message);
    }
}
