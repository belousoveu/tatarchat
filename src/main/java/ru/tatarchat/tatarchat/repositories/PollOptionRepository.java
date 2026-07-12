package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.PollOption;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
}