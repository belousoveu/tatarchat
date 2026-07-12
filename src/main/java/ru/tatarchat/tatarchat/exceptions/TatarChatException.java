package ru.tatarchat.tatarchat.exceptions;

import lombok.Getter;

@Getter
public class TatarChatException extends RuntimeException {
    private final String errorCode;

    public TatarChatException(String message) {
        this(message, "INTERNAL_ERROR");
    }

    public TatarChatException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}