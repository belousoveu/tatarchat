package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tatarchat.tatarchat.entities.embeddable.ChatMemberId;

import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember {

    @EmbeddedId
    private ChatMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", updatable = false)
    private OffsetDateTime joinedAt;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;
}