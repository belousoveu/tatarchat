package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tatarchat.tatarchat.enums.ChatType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chats_seq")
    @SequenceGenerator(name = "chats_seq", sequenceName = "chats_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 20)
    private ChatType chatType;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", updatable = false)
    private OffsetDateTime updatedAt;

    // в классе Chat добавим:
    @Column(name = "last_message_id")
    private Long lastMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id", insertable = false, updatable = false)
    private Message lastMessage;

    // Связь с участниками (для удобства)
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMember> members = new ArrayList<>();
}