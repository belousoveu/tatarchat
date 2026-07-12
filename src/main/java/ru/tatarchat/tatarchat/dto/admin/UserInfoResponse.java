package ru.tatarchat.tatarchat.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String email;
    private String displayName;
    private String role;
    private boolean enabled;
    private String avatarUrl;
    private OffsetDateTime createdAt;
    private Long inviteCodeId;
}