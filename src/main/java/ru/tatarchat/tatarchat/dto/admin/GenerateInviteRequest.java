package ru.tatarchat.tatarchat.dto.admin;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class GenerateInviteRequest {
    @Min(1)
    private Integer maxUses;
    private OffsetDateTime expiresAt; // может быть null (бессрочный)
}