package com.solbeg.newsservice.enams;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    NEWS_NOT_FOUND("News not found with "),
    COMMENT_NOT_FOUND("Comment not found with "),
    ERROR_CREATE_OBJECT("News didn't create!"),
    ERROR_CHANGE("You have no right to change the data of other users!");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}