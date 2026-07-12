package ru.tatarchat.tatarchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class TatarChatApplicationConfig {

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
