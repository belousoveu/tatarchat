package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_seq")
    @SequenceGenerator(name = "messages_seq", sequenceName = "messages_seq", allocationSize = 1)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited;

    @Column(name = "edited_at")
    private OffsetDateTime editedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "email_message_id")
    private String emailMessageId;

    // Связь с вложениями (OneToMany)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    // Связь с реакциями (OneToMany)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reaction> reactions = new ArrayList<>();

    // Связь с прочтениями (OneToMany)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReadReceipt> readReceipts = new ArrayList<>();
}