package ru.tatarchat.tatarchat.exceptions;

public class InvalidOperationException extends TatarChatException {
    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION");
    }
}
