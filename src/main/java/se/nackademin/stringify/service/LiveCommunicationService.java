package se.nackademin.stringify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.nackademin.stringify.controller.response.ConnectionNotice;
import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Message;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.dto.ProfileDto;
import se.nackademin.stringify.exception.ChatSessionNotFoundException;
import se.nackademin.stringify.exception.ProfileNotFoundException;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.MessageRepository;
import se.nackademin.stringify.repository.ProfileRepository;
import se.nackademin.stringify.util.DateUtil;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

/**
 * A services to handle live communication business logic
 */
@Service
@RequiredArgsConstructor
public class LiveCommunicationService implements IService {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ProfileRepository profileRepository;

    /**
     * Stores all messages received from clients.
     *
     * @param chatSessionGuid The active ChatSession Id.
     * @param message         The message sent from the client.
     * @return {@code Message.class}
     */
    public Message storeMessage(UUID chatSessionGuid, @Valid Message message) {
        ChatSession chatSession = getChatSession(chatSessionGuid);

        message.setDate(DateUtil.now());
        message.setId(UUID.randomUUID());
        message.setGuid(UUID.randomUUID());
        message.setChatSession(chatSession);
        return messageRepository.save(message);
    }

    /**
     * Persists connected client's profile to the database.
     *
     * @param chatSessionGuid The active ChatSession Id.
     * @param profile         The client's profile
     * @return {@code ConnectionNotice.class}
     */
    public ConnectionNotice storeProfileConnected(UUID chatSessionGuid, @Valid Profile profile) {
        ChatSession chatSession = getChatSession(chatSessionGuid);
        ProfileDto connectedProfile;
        Optional<Profile> optionalProfile = profileRepository.findByGuid(profile.getGuid());

        if (optionalProfile.isEmpty()) {
            profile.setId(UUID.randomUUID());
            profile.setChatSession(chatSession);
            connectedProfile = profileRepository.save(profile).convertToDto();
        } else
            connectedProfile = optionalProfile.get().convertToDto();

        Message message = Message.builder()
                .guid(UUID.randomUUID())
                .id(UUID.randomUUID())
                .chatSession(chatSession)
                .date(DateUtil.now())
                .avatar("connect")
                .sender("Notice")
                .content(connectedProfile.getName() + " has connected to the meeting.")
                .build();

        Message messageToSend = messageRepository.save(message);

        return new ConnectionNotice(connectedProfile, messageToSend.convertToDto());
    }

    /**
     * Deletes disconnected profiles from the database and deletes the active ChatSession
     * if no profiles are connected.
     *
     * @param chatSessionGuid The active ChatSession Id.
     * @param profile         The client's profile
     * @return {@code ConnectionNotice.class}
     */
    @Transactional
    public ConnectionNotice removeProfileDisconnected(UUID chatSessionGuid, @Valid Profile profile) {

        Profile profileFound = profileRepository.findAllByGuidAndChatSession_Guid(profile.getGuid(), chatSessionGuid)
                .orElseThrow(() -> new ProfileNotFoundException("Could not find a profile"));
        ChatSession chatSession = getChatSession(chatSessionGuid);

        chatSession.getProfilesConnected().remove(profileFound);
        profileRepository.deleteById(profileFound.getId());

        boolean deleted = false;
        if (chatSession.getProfilesConnected().size() == 0) {
            deleted = true;
            chatSessionRepository.deleteById(chatSession.getId());
        }

        Message message;
        if (!deleted) {
            message = Message.builder()
                    .guid(UUID.randomUUID())
                    .id(UUID.randomUUID())
                    .chatSession(chatSession)
                    .date(DateUtil.now())
                    .avatar("disconnect")
                    .sender("Notice")
                    .content(profile.getName() + " has disconnected from the meeting.")
                    .build();
            message = messageRepository.save(message);
            return new ConnectionNotice(profile.convertToDto(), message.convertToDto());
        }

        return null;
    }

    /**
     * Gets a ChatSession from the database.
     *
     * @param chatSessionGuid Id of an active ChatSession
     * @return {@code ChatSession}
     * @throws ChatSessionNotFoundException When a ChatSession cant be found in the database.
     */
    @Override
    public ChatSession getChatSession(UUID chatSessionGuid) throws ChatSessionNotFoundException {
        return chatSessionRepository.findByGuid(chatSessionGuid)
                .orElseThrow(() -> new ChatSessionNotFoundException(
                        String.format("No meetings with the id %s was found.", chatSessionGuid)
                ));
    }
}
