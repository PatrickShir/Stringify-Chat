package se.nackademin.stringify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.nackademin.stringify.domain.ChatSession;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository for ChatSession
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    Optional<ChatSession> findByGuid(UUID guid);

    Optional<ChatSession> findByKey(String key);

    boolean existsByGuid(UUID guid);
}
