package ru.tatarchat.tatarchat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarchat.tatarchat.dto.admin.GenerateInviteRequest;
import ru.tatarchat.tatarchat.dto.admin.InviteCodeResponse;
import ru.tatarchat.tatarchat.dto.admin.UpdateUserRequest;
import ru.tatarchat.tatarchat.dto.admin.UserInfoResponse;
import ru.tatarchat.tatarchat.entities.InviteCode;
import ru.tatarchat.tatarchat.entities.User;
import ru.tatarchat.tatarchat.exceptions.InvalidOperationException;
import ru.tatarchat.tatarchat.exceptions.TatarChatAccessDeniedException;
import ru.tatarchat.tatarchat.exceptions.UserNotFoundException;
import ru.tatarchat.tatarchat.mappers.InviteCodeMapper;
import ru.tatarchat.tatarchat.mappers.UserMapper;
import ru.tatarchat.tatarchat.repositories.InviteCodeRepository;
import ru.tatarchat.tatarchat.repositories.UserRepository;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    @Value("${app.invite.code-length:8}")
    private int codeLength;

    private final UserRepository userRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final InviteCodeMapper inviteCodeMapper;
    private final UserMapper userMapper;
    private final SecureRandom secureRandom;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    public InviteCodeResponse generateInviteCode(Long adminId, GenerateInviteRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));

        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new TatarChatAccessDeniedException("Только администратор может генерировать инвайт-коды");
        }

        String code = generateRandomCode();
        InviteCode inviteCode = InviteCode.builder()
                .code(code)
                .createdByUserId(adminId)
                .maxUses(request.getMaxUses())
                .usesRemaining(request.getMaxUses())
                .expiresAt(request.getExpiresAt())
                .build();

        InviteCode saved = inviteCodeRepository.save(inviteCode);
        log.info("Создан инвайт-код {} администратором {}", code, adminId);
        return inviteCodeMapper.toInviteCodeResponse(saved);
    }

    @Transactional
    public UserInfoResponse blockUser(Long adminId, Long userId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new TatarChatAccessDeniedException("Только администратор может блокировать пользователей");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getId().equals(adminId)) {
            throw new InvalidOperationException("Нельзя заблокировать себя");
        }

        if (!user.isEnabled()) {
            throw new InvalidOperationException("Пользователь уже заблокирован");
        }

        user.setEnabled(false);
        User updated = userRepository.save(user);
        log.info("Пользователь {} заблокирован администратором {}", userId, adminId);
        return userMapper.toUserResponse(updated);
    }

    @Transactional
    public UserInfoResponse unblockUser(Long adminId, Long userId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new TatarChatAccessDeniedException("Только администратор может разблокировать пользователей");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.isEnabled()) {
            throw new InvalidOperationException("Пользователь уже активен");
        }

        user.setEnabled(true);
        User updated = userRepository.save(user);
        log.info("Пользователь {} разблокирован администратором {}", userId, adminId);
        return userMapper.toUserResponse(updated);
    }

    public Page<UserInfoResponse> getAllUsers(Long adminId, Pageable pageable) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new TatarChatAccessDeniedException("Только администратор может просматривать список пользователей");
        }
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Transactional
    public UserInfoResponse updateUser(Long adminId, Long userId, UpdateUserRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException(adminId));
        if (!"ROLE_ADMIN".equals(admin.getRole())) {
            throw new TatarChatAccessDeniedException("Только администратор может обновлять данные пользователей");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (request.getDisplayName() != null && !request.getDisplayName().isBlank()) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getRole() != null) {
            if (!request.getRole().startsWith("ROLE_")) {
                throw new InvalidOperationException("Роль должна начинаться с 'ROLE_'");
            }
            
            if (userId.equals(adminId) && "ROLE_ADMIN".equals(user.getRole()) && 
                    !"ROLE_ADMIN".equals(request.getRole())) {
                throw new InvalidOperationException("Нельзя снять роль администратора с самого себя");
            }
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            if (userId.equals(adminId) && !request.getEnabled()) {
                throw new InvalidOperationException("Нельзя заблокировать самого себя через этот метод (используйте отдельный метод)");
            }
            user.setEnabled(request.getEnabled());
        }

        User updated = userRepository.save(user);
        log.info("Данные пользователя {} обновлены администратором {}", userId, adminId);
        return userMapper.toUserResponse(updated);
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            sb.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}