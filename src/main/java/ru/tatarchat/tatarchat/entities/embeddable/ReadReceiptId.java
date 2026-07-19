package ru.tatarchat.tatarchat.entities.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptId implements Serializable {
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "user_id")
    private Long userId;
}