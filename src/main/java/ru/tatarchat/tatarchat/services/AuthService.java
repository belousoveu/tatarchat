package ru.tatarchat.tatarchat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarchat.tatarchat.dto.AuthResponse;
import ru.tatarchat.tatarchat.dto.LoginRequest;
import ru.tatarchat.tatarchat.dto.RegisterRequest;
import ru.tatarchat.tatarchat.entities.InviteCode;
import ru.tatarchat.tatarchat.entities.User;
import ru.tatarchat.tatarchat.repositories.InviteCodeRepository;
import ru.tatarchat.tatarchat.repositories.UserRepository;
import ru.tatarchat.tatarchat.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        InviteCode inviteCode = inviteCodeRepository.findByCode(request.getInviteCode())
                .orElseThrow(() -> new RuntimeException("Неверный инвайт-код"));

        if (!inviteCodeRepository.isValid(inviteCode)) {
            throw new RuntimeException("Инвайт-код просрочен или уже использован");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .role("ROLE_USER")
                .isEnabled(true)
                .inviteCodeId(inviteCode.getId())
                .build();

        User savedUser = userRepository.save(user);

        int updated = inviteCodeRepository.decrementUsesRemaining(inviteCode.getId());
        if (updated == 0) {
            log.warn("Не удалось уменьшить usesRemaining для кода id={}", inviteCode.getId());
        }

        String token = jwtTokenProvider.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getDisplayName(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole()
        );
    }
}