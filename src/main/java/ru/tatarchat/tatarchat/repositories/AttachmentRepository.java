package ru.tatarchat.tatarchat.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {}