package ru.tatarchat.tatarchat.exceptions;

public class UserNotFoundException extends TatarChatException {

    public UserNotFoundException(Long userId) {
        super("Пользователь с ID " + userId + " не найден", "USER_NOT_FOUND");
    }
    public UserNotFoundException(String email) {
        super("Пользователь с email " + email + " не найден", "USER_NOT_FOUND");
    }
}
