package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.PollVote;
import ru.tatarchat.tatarchat.entities.embeddable.PollVoteId;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, PollVoteId> {
}