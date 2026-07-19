package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.ReadReceipt;
import ru.tatarchat.tatarchat.entities.embeddable.ReadReceiptId;

import java.util.List;

@Repository
public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, ReadReceiptId> {
    List<ReadReceipt> findByMessageId(Long messageId);
}