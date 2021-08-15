package se.nackademin.stringify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.nackademin.stringify.controller.response.Meeting;
import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.exception.ChatSessionNotFoundException;
import se.nackademin.stringify.exception.ConnectionLimitException;
import se.nackademin.stringify.exception.InvalidKeyException;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.ProfileRepository;
import se.nackademin.stringify.util.Key;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;


/**
 * A service class for handling chatSessions.
 */
@Service
@RequiredArgsConstructor
public class MeetingService implements IService {

    private final ChatSessionRepository chatSessionRepository;
    private final ProfileRepository profileRepository;

    /**
     * Creates a new chatSession and persists it to the database together with the given profile.
     *
     * @param profile to start a new chat session
     * @return a response object {@code Meeting.class} containing the persisted chatSession and profile
     */
    public Meeting createNewMeeting(@Valid Profile profile) {

        ChatSession savedChatSession = chatSessionRepository.save(new ChatSession());
        savedChatSession.setConnectUrl("https://stringify-chat.netlify.app/connect?chat-id=" + savedChatSession.getGuid());
        savedChatSession.setKey(Key.generate().toString());
        ChatSession meeting = chatSessionRepository.save(savedChatSession);

        profile.setChatSession(meeting);
        Profile connectedProfile = profileRepository.save(profile);

        return new Meeting(connectedProfile.convertToDto(), meeting.convertToDto());
    }

    /**
     * Finds and returns a ChatSession from the database if any chatSession exists with the given key.
     *
     * @param key The key identified with the chatSession.
     * @return Returns a chatSession to connect with.
     * @throws ChatSessionNotFoundException When a chat session with the given key could not be found.
     * @throws ConnectionLimitException     When the chatSession already has too many connections. (limit: 5)
     * @throws InvalidKeyException          When the key format is invalid.
     */
    @Transactional(readOnly = true)
    public ChatSession getChatSessionByKey(String key) {

        if (!Key.isValidKey(key))
            throw new InvalidKeyException();

        ChatSession chatSession = chatSessionRepository.findByKey(key)
                .orElseThrow(() -> new ChatSessionNotFoundException(
                        String.format("No meetings with the key %s was found.", key)));

        if (chatSession.getProfilesConnected().size() == 5)
            throw new ConnectionLimitException("Meeting has reached the maximum number of connections.");

        return chatSession;
    }

    /**
     * Finds and returns a ChatSession from the database if any chatSession exists with the given guid.
     *
     * @param chatId The guid identified with the chatSession.
     * @return Returns a chatSession to connect with.
     * @throws ChatSessionNotFoundException When a chat session with the given chatId could not be found.
     * @throws ConnectionLimitException     When the chatSession already has too many connections. (limit: 5)
     */
    @Transactional(readOnly = true)
    public ChatSession getMeetingByGuid(UUID chatId) throws ChatSessionNotFoundException, ConnectionLimitException {
        ChatSession chatSession = getChatSession(chatId);

        if (chatSession.getProfilesConnected().size() == 5)
            throw new ConnectionLimitException("Meeting has reached the maximum number of connections.");

        return chatSession;
    }

    /**
     * Retrieves profiles connected to a particular ChatSession from
     * the database.
     *
     * @param chatId The id of an active ChatSession
     * @return A list of profiles.
     */
    public List<Profile> getProfilesConnected(UUID chatId) {
        ChatSession chatSession = getChatSession(chatId);

        return profileRepository.findAllByChatSession_Id(chatSession.getId());
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
