package se.nackademin.stringify.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import se.nackademin.stringify.AbstractIntegrationTest;
import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Message;
import se.nackademin.stringify.dto.MessageDto;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.MessageRepository;
import se.nackademin.stringify.util.Key;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    private ChatSession mockChatSession;

    @BeforeEach
    void setUp() {
        mockChatSession = ChatSession.builder()
                .id(UUID.randomUUID())
                .guid(UUID.randomUUID())
                .key(Key.generate().toString())
                .build();

        mockChatSession = chatSessionRepository.save(mockChatSession);
    }

    @DisplayName("Chat session with no history of messages should return empty list")
    @Test
    void chatSessionWithNoMessagesShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/messages/history")
                .contentType(MediaType.APPLICATION_JSON)
                .param("chat-id", mockChatSession.getGuid().toString())
                .param("page", "0"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonList = result.getResponse().getContentAsString();

        List<MessageDto> messages = new ObjectMapper().readValue(jsonList, new TypeReference<List<MessageDto>>() {
        });

        assertThat(messages).hasSize(0);
    }

    @DisplayName("Non existing chat session should return NOT FOUND")
    @Test
    void nonExistentChatSessionShouldReturnNOT_FOUND() throws Exception {
        mockMvc.perform(get("/api/messages/history")
                .contentType(MediaType.APPLICATION_JSON)
                .param("chat-id", UUID.randomUUID().toString())
                .param("page", "0"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Chat session with history of messages should return list greater than 0")
    @Test
    void chatSessionWithWithHistoryOfMessagesShouldReturnListGreaterThan0() throws Exception {
        for (int i = 0; i < 10; i++) {
            Message message = Message.builder()
                    .id(UUID.randomUUID())
                    .guid(UUID.randomUUID())
                    .sender("John Doe " + i)
                    .content("Hello " + i)
                    .avatar("avatar" + i)
                    .chatSession(mockChatSession)
                    .date(new Timestamp(new Date().getTime()))
                    .build();

            messageRepository.save(message);
        }

        MvcResult result = mockMvc.perform(get("/api/messages/history")
                .contentType(MediaType.APPLICATION_JSON)
                .param("chat-id", mockChatSession.getGuid().toString())
                .param("page", "0"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonList = result.getResponse().getContentAsString();

        List<MessageDto> messages = new ObjectMapper().readValue(jsonList, new TypeReference<List<MessageDto>>() {
        });

        assertThat(messages.size()).isGreaterThan(0);
    }
}
