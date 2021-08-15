package se.nackademin.stringify;

import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Message;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.util.DateUtil;
import se.nackademin.stringify.util.Key;

import java.util.UUID;

/**
 * A class for providing mock data for tests.
 */
public class MockData {

    /**
     * Method provides a mocked ChatSession instance without relation to messages or profiles
     * @return Mocked {@code ChatSession.class}
     */
    public static ChatSession getMockChatSessionEntity() {

        UUID chatId = UUID.randomUUID();
        return  ChatSession.builder()
                .guid(chatId)
                .key(Key.generate().toString())
                .connectUrl("https://stringify-chat.netlify.app/connect?chat-id="+chatId)
                .build();
    }

    /**
     * Method provides a mocked Profile instance without relation to ChatSession
     * @return Mocked {@code Profile.class}
     */
    public static Profile getMockProfileEntity() {
        return Profile.builder()
                .id(UUID.randomUUID())
                .guid(UUID.randomUUID())
                .name("John Doe")
                .avatar("avatar1")
                .build();
    }

    /**
     * Method provides a mocked Message instance without relation to ChatSession
     * @return Mocked {@code Message.class}
     */
    public static Message getMockMessageEntity() {
        return Message.builder()
                .id(UUID.randomUUID())
                .sender("John Doe")
                .avatar("avatar1")
                .date(DateUtil.now())
                .content("Placeholder")
                .build();
    }
}
