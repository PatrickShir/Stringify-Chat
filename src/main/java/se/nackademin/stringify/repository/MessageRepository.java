package se.nackademin.stringify.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.nackademin.stringify.domain.Message;

import java.util.List;
import java.util.UUID;

/**
 * A repository for Message
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChatSession_Guid(UUID chatSessionGuid, Pageable pageable);
}
