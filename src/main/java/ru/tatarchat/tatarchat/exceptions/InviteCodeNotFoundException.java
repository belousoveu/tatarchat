package ru.tatarchat.tatarchat.exceptions;

public class InviteCodeNotFoundException extends TatarChatException{
    public InviteCodeNotFoundException(String code) {
        super("Инвайт-код '" + code + "' не найден", "INVITE_NOT_FOUND");
    }
}
