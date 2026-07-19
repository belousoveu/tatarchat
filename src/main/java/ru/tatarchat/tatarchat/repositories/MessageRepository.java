package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.Message;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long chatId, Pageable pageable);

    // Для получения последнего сообщения в чате
    Optional<Message> findFirstByChatIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long chatId);
}