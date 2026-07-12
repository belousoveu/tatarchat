package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarchat.tatarchat.entities.ChatMember;
import ru.tatarchat.tatarchat.entities.embeddable.ChatMemberId;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
}