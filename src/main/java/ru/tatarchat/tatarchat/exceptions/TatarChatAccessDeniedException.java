package ru.tatarchat.tatarchat.exceptions;

public class TatarChatAccessDeniedException extends TatarChatException {
    public TatarChatAccessDeniedException(String message) {
        super(message, "ACCESS_DENIED");
    }
}
