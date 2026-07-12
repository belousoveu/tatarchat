package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "invite_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invite_codes_seq")
    @SequenceGenerator(name = "invite_codes_seq", sequenceName = "invite_codes_seq", allocationSize = 1)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses_remaining")
    private Integer usesRemaining;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}