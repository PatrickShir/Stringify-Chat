package se.nackademin.stringify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.nackademin.stringify.domain.Message;
import se.nackademin.stringify.exception.ChatSessionNotFoundException;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.MessageRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * A service class used for handling messages.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;

    /**
     * Fetches a list of maximum 10 message objects from the database with use of Pagination.
     * The list is sorted by ascending dates.
     *
     * @param chatGuid the {@code UUID} chat guid in relation with the messages
     * @param page     The of page number of a list of messages
     * @return A list of messages ({@code List<Message>}) with a size of 10
     */
    @Transactional(readOnly = true)
    public List<Message> getMessage(UUID chatGuid, int page) throws ChatSessionNotFoundException {
        final int AMOUNT_OF_ELEMENTS = 10;

        if (!chatSessionRepository.existsByGuid(chatGuid))
            throw new ChatSessionNotFoundException(String.format("No meetings with the id %s was found.", chatGuid));

        Pageable sortedByDate =
                PageRequest.of(page, AMOUNT_OF_ELEMENTS, Sort.by("date").descending());

        return messageRepository.findAllByChatSession_Guid(chatGuid, sortedByDate).stream()
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }

}
