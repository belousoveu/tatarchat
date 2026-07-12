package ru.tatarchat.tatarchat.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты AdminService")
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InviteCodeRepository inviteCodeRepository;

    @Mock
    private InviteCodeMapper inviteCodeMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecureRandom secureRandom;

    @InjectMocks
    private AdminService adminService;

    private static final Long ADMIN_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String USER_EMAIL = "user@example.com";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String USER_ROLE = "ROLE_USER";

    private User adminUser;
    private User normalUser;
    private UserInfoResponse userInfoResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminService, "codeLength", 8);

        adminUser = User.builder()
                .id(ADMIN_ID)
                .email(ADMIN_EMAIL)
                .displayName("Admin")
                .role(ADMIN_ROLE)
                .isEnabled(true)
                .build();

        normalUser = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .displayName("User")
                .role(USER_ROLE)
                .isEnabled(true)
                .build();

        userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(USER_ID);
        userInfoResponse.setEmail(USER_EMAIL);
        userInfoResponse.setDisplayName("User");
        userInfoResponse.setRole(USER_ROLE);
        userInfoResponse.setEnabled(true);
    }

    // ---------- generateInviteCode ----------
    @Test
    @DisplayName("generateInviteCode: успешная генерация")
    void generateInviteCode_Success() {
        // given
        GenerateInviteRequest request = new GenerateInviteRequest();
        request.setMaxUses(5);
        request.setExpiresAt(OffsetDateTime.now().plusDays(1));

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(secureRandom.nextInt(36)).thenReturn(0, 1, 2, 3, 4, 5, 6, 7);

        // Используем ArgumentCaptor для проверки сохраняемого объекта
        ArgumentCaptor<InviteCode> inviteCodeCaptor = ArgumentCaptor.forClass(InviteCode.class);
        when(inviteCodeRepository.save(inviteCodeCaptor.capture())).thenAnswer(invocation -> {
            InviteCode saved = invocation.getArgument(0);
            // Имитируем, что БД присвоила ID
            saved.setId(100L);
            return saved;
        });

        InviteCodeResponse expectedResponse = new InviteCodeResponse();
        expectedResponse.setId(100L);
        expectedResponse.setCode("ABCDEFGH");
        expectedResponse.setMaxUses(5);
        expectedResponse.setUsesRemaining(5);
        when(inviteCodeMapper.toInviteCodeResponse(any(InviteCode.class))).thenReturn(expectedResponse);

        // when
        InviteCodeResponse response = adminService.generateInviteCode(ADMIN_ID, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("ABCDEFGH");
        assertThat(response.getMaxUses()).isEqualTo(5);

        // Проверяем, что сохраняемый объект заполнен корректно
        InviteCode captured = inviteCodeCaptor.getValue();
        assertThat(captured.getCode()).isEqualTo("ABCDEFGH");
        assertThat(captured.getCreatedByUserId()).isEqualTo(ADMIN_ID);
        assertThat(captured.getMaxUses()).isEqualTo(5);
        assertThat(captured.getUsesRemaining()).isEqualTo(5);
        assertThat(captured.getExpiresAt()).isEqualTo(request.getExpiresAt());

        verify(inviteCodeRepository, times(1)).save(any(InviteCode.class));
        verify(inviteCodeMapper, times(1)).toInviteCodeResponse(any(InviteCode.class));
    }

    @Test
    @DisplayName("generateInviteCode: администратор не найден")
    void generateInviteCode_AdminNotFound() {
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminService.generateInviteCode(ADMIN_ID, new GenerateInviteRequest()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с ID " + ADMIN_ID + " не найден");
    }

    @Test
    @DisplayName("generateInviteCode: пользователь не является администратором")
    void generateInviteCode_NotAdmin() {
        User nonAdmin = User.builder().id(3L).role("ROLE_USER").build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(nonAdmin));
        assertThatThrownBy(() -> adminService.generateInviteCode(3L, new GenerateInviteRequest()))
                .isInstanceOf(TatarChatAccessDeniedException.class)
                .hasMessage("Только администратор может генерировать инвайт-коды");
    }

    // ---------- blockUser ----------
    @Test
    @DisplayName("blockUser: успешная блокировка")
    void blockUser_Success() {
        // given
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Настраиваем маппер на сохранённый объект
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserInfoResponse resp = new UserInfoResponse();
            resp.setId(u.getId());
            resp.setEmail(u.getEmail());
            resp.setDisplayName(u.getDisplayName());
            resp.setRole(u.getRole());
            resp.setEnabled(u.isEnabled());
            return resp;
        });

        // when
        UserInfoResponse response = adminService.blockUser(ADMIN_ID, USER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isEnabled()).isFalse();

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.isEnabled()).isFalse(); // Проверяем, что флаг изменился
        assertThat(savedUser.getId()).isEqualTo(USER_ID);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("blockUser: попытка блокировки самого себя")
    void blockUser_SelfBlock() {
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        assertThatThrownBy(() -> adminService.blockUser(ADMIN_ID, ADMIN_ID))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Нельзя заблокировать себя");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("blockUser: пользователь уже заблокирован")
    void blockUser_AlreadyBlocked() {
        normalUser.setEnabled(false);
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        assertThatThrownBy(() -> adminService.blockUser(ADMIN_ID, USER_ID))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Пользователь уже заблокирован");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("blockUser: пользователь не найден")
    void blockUser_UserNotFound() {
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.blockUser(ADMIN_ID, USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с ID " + USER_ID + " не найден");
    }

    // ---------- unblockUser ----------
    @Test
    @DisplayName("unblockUser: успешная разблокировка")
    void unblockUser_Success() {
        // given
        normalUser.setEnabled(false);
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserInfoResponse resp = new UserInfoResponse();
            resp.setEnabled(u.isEnabled());
            return resp;
        });

        // when
        UserInfoResponse response = adminService.unblockUser(ADMIN_ID, USER_ID);

        // then
        assertThat(response.isEnabled()).isTrue();

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.isEnabled()).isTrue();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("unblockUser: пользователь уже активен")
    void unblockUser_AlreadyActive() {
        normalUser.setEnabled(true);
        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        assertThatThrownBy(() -> adminService.unblockUser(ADMIN_ID, USER_ID))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Пользователь уже активен");
        verify(userRepository, never()).save(any());
    }

    // ---------- getAllUsers ----------
    @Test
    @DisplayName("getAllUsers: успешное получение списка")
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(normalUser));

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toUserResponse(normalUser)).thenReturn(userInfoResponse);

        Page<UserInfoResponse> result = adminService.getAllUsers(ADMIN_ID, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("getAllUsers: пользователь не админ")
    void getAllUsers_NotAdmin() {
        User nonAdmin = User.builder().id(3L).role("ROLE_USER").build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(nonAdmin));

        assertThatThrownBy(() -> adminService.getAllUsers(3L, PageRequest.of(0, 10)))
                .isInstanceOf(TatarChatAccessDeniedException.class)
                .hasMessage("Только администратор может просматривать список пользователей");
    }

    // ---------- updateUser ----------
    @Test
    @DisplayName("updateUser: успешное обновление displayName")
    void updateUser_UpdateDisplayName() {
        // given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setDisplayName("New Name");

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserInfoResponse resp = new UserInfoResponse();
            resp.setDisplayName(u.getDisplayName());
            return resp;
        });

        // when
        UserInfoResponse response = adminService.updateUser(ADMIN_ID, USER_ID, request);

        // then
        assertThat(response.getDisplayName()).isEqualTo("New Name");

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getDisplayName()).isEqualTo("New Name");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser: успешное обновление роли")
    void updateUser_UpdateRole() {
        // given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("ROLE_ADMIN");

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        when(userMapper.toUserResponse(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserInfoResponse resp = new UserInfoResponse();
            resp.setRole(u.getRole());
            return resp;
        });

        // when
        UserInfoResponse response = adminService.updateUser(ADMIN_ID, USER_ID, request);

        // then
        assertThat(response.getRole()).isEqualTo("ROLE_ADMIN");

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getRole()).isEqualTo("ROLE_ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser: попытка снять админ-роль с самого себя")
    void updateUser_RemoveOwnAdminRole() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("ROLE_USER");

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> adminService.updateUser(ADMIN_ID, ADMIN_ID, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Нельзя снять роль администратора с самого себя");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser: попытка заблокировать себя через update")
    void updateUser_SelfBlockThroughUpdate() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEnabled(false);

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> adminService.updateUser(ADMIN_ID, ADMIN_ID, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Нельзя заблокировать самого себя через этот метод (используйте отдельный метод)");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser: некорректный формат роли")
    void updateUser_InvalidRoleFormat() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("ADMIN");

        when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(normalUser));

        assertThatThrownBy(() -> adminService.updateUser(ADMIN_ID, USER_ID, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessage("Роль должна начинаться с 'ROLE_'");
        verify(userRepository, never()).save(any());
    }
}