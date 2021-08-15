package se.nackademin.stringify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.nackademin.stringify.domain.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for Profile
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByGuid(UUID guid);

    Optional<Profile> findAllByGuidAndChatSession_Guid(UUID guid, UUID chatSession_guid);

    List<Profile> findAllByChatSession_Id(UUID chatSession_id);
}
