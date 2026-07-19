package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.tatarchat.tatarchat.entities.embeddable.ReadReceiptId;

import java.time.OffsetDateTime;

@Entity
@Table(name = "read_receipts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceipt {

    @EmbeddedId
    private ReadReceiptId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;


    @CreationTimestamp
    @Column(name = "read_at", updatable = false)
    private OffsetDateTime readAt;
}