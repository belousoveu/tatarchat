package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "message_edits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoIncrement
    private Long id;

    @Column(name = "email_message_id", nullable = false, length = 255)
    private String emailMessageId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "edited_by_user_id", nullable = false)
    private Long editedByUserId;

    @Column(name = "new_text", nullable = false, columnDefinition = "TEXT")
    private String newText;

    @Column(name = "edited_at", updatable = false)
    private OffsetDateTime editedAt;
}