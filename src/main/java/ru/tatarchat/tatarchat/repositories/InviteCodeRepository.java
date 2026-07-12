package ru.tatarchat.tatarchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarchat.tatarchat.entities.InviteCode;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    Optional<InviteCode> findByCode(String code);

    @Modifying
    @Transactional
    @Query("UPDATE InviteCode ic SET ic.usesRemaining = ic.usesRemaining - 1 WHERE ic.id = :id AND ic.usesRemaining > 0")
    int decrementUsesRemaining(@Param("id") Long id);

    default boolean isValid(InviteCode code) {
        if (code == null) return false;
        if (code.getExpiresAt() != null && code.getExpiresAt().isBefore(OffsetDateTime.now())) {
            return false;
        }
        if (code.getMaxUses() != null && code.getUsesRemaining() == null) {
            return false;
        }
        return code.getMaxUses() == null || code.getUsesRemaining() > 0;
    }
}
