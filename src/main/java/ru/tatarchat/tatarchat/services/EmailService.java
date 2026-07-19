package ru.tatarchat.tatarchat.services;

import ru.tatarchat.tatarchat.dto.EmailMessageDto;

import java.util.List;
import java.util.Map;

public interface EmailService {
    /**
     * Отправляет письмо.
     * @param toEmail адрес получателя
     * @param subject тема письма
     * @param body текст письма (может быть HTML или plain text)
     * @param attachments карта с именем файла и его содержимым (опционально)
     */
    void sendEmail(String toEmail, String subject, String body, Map<String, byte[]> attachments);

    /**
     * Проверяет наличие новых писем в почтовом ящике.
     * @return список новых писем
     */
    List<EmailMessageDto> fetchNewEmails();

    /**
     * Помечает письмо как прочитанное или удалённое.
     * @param messageId уникальный идентификатор письма у провайдера
     */
    void markEmailAsRead(String messageId);
    void deleteEmail(String messageId);

}