package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "user_id", "emoji"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reactions_seq")
    @SequenceGenerator(name = "reactions_seq", sequenceName = "reactions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "emoji", nullable = false, length = 10)
    private String emoji;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}