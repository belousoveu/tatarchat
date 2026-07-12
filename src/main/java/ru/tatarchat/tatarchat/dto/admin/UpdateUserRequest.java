package ru.tatarchat.tatarchat.dto.admin;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 100)
    private String displayName;
    private String role; // "ROLE_USER" или "ROLE_ADMIN"
    private Boolean enabled;
}