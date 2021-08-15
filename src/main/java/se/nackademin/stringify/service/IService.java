package se.nackademin.stringify.service;

import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.exception.ChatSessionNotFoundException;

import java.util.UUID;

/**
 * An interface for finding and returning a chatSession
 */
public interface IService {

    ChatSession getChatSession(UUID chatSessionGuid) throws ChatSessionNotFoundException;
}
