package ru.tatarchat.tatarchat.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tatarchat.tatarchat.dto.AuthResponse;
import ru.tatarchat.tatarchat.dto.LoginRequest;
import ru.tatarchat.tatarchat.dto.RegisterRequest;
import ru.tatarchat.tatarchat.entities.InviteCode;
import ru.tatarchat.tatarchat.entities.User;
import ru.tatarchat.tatarchat.repositories.InviteCodeRepository;
import ru.tatarchat.tatarchat.repositories.UserRepository;
import ru.tatarchat.tatarchat.security.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InviteCodeRepository inviteCodeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private InviteCode validInviteCode;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");
        registerRequest.setInviteCode("INVITE123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        validInviteCode = new InviteCode();
        validInviteCode.setId(1L);
        validInviteCode.setCode("INVITE123");
        validInviteCode.setMaxUses(5);
        validInviteCode.setUsesRemaining(3);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .displayName("Test User")
                .role("ROLE_USER")
                .isEnabled(true)
                .build();
    }

    @Test
    void registerSuccess() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(inviteCodeRepository.findByCode(registerRequest.getInviteCode()))
                .thenReturn(Optional.of(validInviteCode));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(inviteCodeRepository.decrementUsesRemaining(validInviteCode.getId())).thenReturn(1);
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn("jwt-token");
        when(inviteCodeRepository.isValid(any(InviteCode.class))).thenReturn(true);

        // when
        AuthResponse response = authService.register(registerRequest);

        // then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getDisplayName());
        assertEquals("ROLE_USER", response.getRole());

        verify(userRepository, times(1)).save(any(User.class));
        verify(inviteCodeRepository, times(1)).decrementUsesRemaining(validInviteCode.getId());
    }

    @Test
    void registerUserAlreadyExists() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("Email уже зарегистрирован", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerInvalidInviteCode() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(inviteCodeRepository.findByCode(registerRequest.getInviteCode()))
                .thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("Неверный инвайт-код", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerExpiredInviteCode() {
        // given
        validInviteCode.setExpiresAt(java.time.OffsetDateTime.now().minusDays(1)); // просрочен
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(inviteCodeRepository.findByCode(registerRequest.getInviteCode()))
                .thenReturn(Optional.of(validInviteCode));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("Инвайт-код просрочен или уже использован", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSuccess() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user.getEmail())).thenReturn("jwt-token");

        // when
        AuthResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getDisplayName());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void loginUserNotFound() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}