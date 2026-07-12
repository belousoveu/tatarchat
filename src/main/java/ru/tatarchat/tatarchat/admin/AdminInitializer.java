package ru.tatarchat.tatarchat.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tatarchat.tatarchat.entities.User;
import ru.tatarchat.tatarchat.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@relaychat.local}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.display-name:Главный администратор}")
    private String adminDisplayName;

    @Override
    public void run(String @NonNull ... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .displayName(adminDisplayName)
                    .role("ROLE_ADMIN")
                    .isEnabled(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Создан администратор: {}", adminEmail);
        } else {
            log.info("✅ Администратор уже существует: {}", adminEmail);
        }
    }
}