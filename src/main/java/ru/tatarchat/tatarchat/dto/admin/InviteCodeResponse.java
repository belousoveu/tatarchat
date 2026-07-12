package ru.tatarchat.tatarchat.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteCodeResponse {
    private Long id;
    private String code;
    private Integer maxUses;
    private Integer usesRemaining;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
    private Long createdByUserId;
}