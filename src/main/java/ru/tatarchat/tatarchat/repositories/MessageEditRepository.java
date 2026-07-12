package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.MessageEdit;

@Repository
public interface MessageEditRepository extends JpaRepository<MessageEdit, Long> {
}