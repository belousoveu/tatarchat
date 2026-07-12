package ru.tatarchat.tatarchat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tatarchat.tatarchat.entities.embeddable.PollVoteId;

import java.time.OffsetDateTime;

@Entity
@Table(name = "poll_votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollVote {

    @EmbeddedId
    private PollVoteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pollId")
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private PollOption option;

    @Column(name = "voted_at", updatable = false)
    private OffsetDateTime votedAt;
}