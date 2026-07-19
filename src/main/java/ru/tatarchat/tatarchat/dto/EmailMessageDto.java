package ru.tatarchat.tatarchat.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class EmailMessageDto {
    private String id;          // ID письма у провайдера
    private String from;        // отправитель
    private String subject;     // тема
    private String body;        // текст
    private List<AttachmentDto> attachments; // список вложений с именами и ссылками
    private LocalDateTime receivedDate;

    private List<AttachmentDto> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }
}