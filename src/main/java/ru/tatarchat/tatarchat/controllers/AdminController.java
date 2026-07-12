package ru.tatarchat.tatarchat.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tatarchat.tatarchat.dto.admin.GenerateInviteRequest;
import ru.tatarchat.tatarchat.dto.admin.InviteCodeResponse;
import ru.tatarchat.tatarchat.dto.admin.UpdateUserRequest;
import ru.tatarchat.tatarchat.dto.admin.UserInfoResponse;
import ru.tatarchat.tatarchat.entities.User;
import ru.tatarchat.tatarchat.services.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/invite")
    public ResponseEntity<InviteCodeResponse> generateInvite(@AuthenticationPrincipal User currentUser,
                                                             @Valid @RequestBody GenerateInviteRequest request) {
        Long adminId = currentUser.getId();
        return ResponseEntity.ok(adminService.generateInviteCode(adminId, request));
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<UserInfoResponse> blockUser(@AuthenticationPrincipal User currentUser,
                                                      @PathVariable Long userId) {
        Long adminId = currentUser.getId();
        return ResponseEntity.ok(adminService.blockUser(adminId, userId));
    }

    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<UserInfoResponse> unblockUser(@AuthenticationPrincipal User currentUser,
                                                    @PathVariable Long userId) {
        Long adminId = currentUser.getId();
        return ResponseEntity.ok(adminService.unblockUser(adminId, userId));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(@AuthenticationPrincipal User currentUser,
                                                          Pageable pageable) {
        Long adminId = currentUser.getId();
        return ResponseEntity.ok(adminService.getAllUsers(adminId, pageable));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserInfoResponse> updateUser(@AuthenticationPrincipal User currentUser,
                                                   @PathVariable Long userId,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        Long adminId = currentUser.getId();
        return ResponseEntity.ok(adminService.updateUser(adminId, userId, request));
    }
}