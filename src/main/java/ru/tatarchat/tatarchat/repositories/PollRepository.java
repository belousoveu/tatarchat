package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.Poll;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
}