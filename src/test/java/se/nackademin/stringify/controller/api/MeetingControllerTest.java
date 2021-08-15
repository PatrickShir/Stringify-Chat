package se.nackademin.stringify.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import se.nackademin.stringify.AbstractIntegrationTest;
import se.nackademin.stringify.MockData;
import se.nackademin.stringify.controller.response.Meeting;
import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.dto.ProfileDto;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.ProfileRepository;
import se.nackademin.stringify.util.Key;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MeetingControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ChatSessionRepository chatSessionRepository;

    @Autowired
    ProfileRepository profileRepository;

    private ChatSession mockChatSession;
    private ProfileDto profileDto;

    @BeforeEach
    void setUp() {
        UUID guid = UUID.randomUUID();
        mockChatSession = ChatSession.builder()
                .id(UUID.randomUUID())
                .guid(guid)
                .key(Key.generate().toString())
                .connectUrl("https://stringify-chat.netlify.app/profile?connect=" + guid)
                .build();

        profileDto = ProfileDto.builder()
                .avatar("avatar1")
                .name("John Doe")
                .build();

        mockChatSession = chatSessionRepository.save(mockChatSession);
    }

    @DisplayName("The given profile should get persisted to DB together with a ChatSession")
    @Test
    void profileShouldExist_AndCreateNewChatSession_OnDataBase() throws Exception {

        String jsonProfile = new ObjectMapper().writeValueAsString(profileDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/meetings/new-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProfile))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        Meeting meeting = new ObjectMapper().readValue(contentAsString, Meeting.class);

        ChatSession newChatSession = chatSessionRepository.findByKey(meeting.getChatSession().getKey()).get();

        assertThat(newChatSession).isNotNull();
        assertThat(newChatSession.getProfilesConnected().size()).isEqualTo(1);
    }

    @DisplayName("Empty json and empty json object should return BAD REQUEST")
    @ParameterizedTest
    @ValueSource(strings = {"", "{}", "{\"name\": \"\", \"avatar\": \"avatar1\"}"})
    void emptyArgumentShouldReturnBadRequest(String argument) throws Exception {
        mockMvc.perform(post("/api/meetings/new-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .content(argument))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("Valid key should return a chat session")
    @Test
    void validKeyWithExistingChatSessionShouldReturnChatSession() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("key", mockChatSession.getKey()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.guid").value(mockChatSession.getGuid().toString()));
    }

    @DisplayName("chat id with existing chat session should return a chat session")
    @Test
    void chatIdWithExistingChatSessionShouldReturnChatSession() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("chat-id", mockChatSession.getGuid().toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.key").value(mockChatSession.getKey()));
    }

    @DisplayName("Invalid key with grater than 6 characters should return BAD REQUEST")
    @Test
    void invalidKeyWithTooManyCharactersShouldReturnBAD_REQUEST() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("key", "SADAF49PJ"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("Key with non-existing chat session will return NOT FOUND")
    @Test
    void keyWithNonExistingChatSessionShouldReturnNOT_FOUND() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("key", Key.generate().toString()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("chatId with non-existing chat session will return NOT FOUND")
    @Test
    void chatIdWithNonExistingChatSessionShouldReturnNOT_FOUND() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("chat-id", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Chat with maximum amount of connected profiles should return NO CONTENT")
    @Test
    void chatWithMaxConnectedWithKeyProvidedShouldReturnNO_CONTENT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Profile mockProfile = Profile.builder()
                    .id(UUID.randomUUID())
                    .guid(UUID.randomUUID())
                    .name("John Doe " + i)
                    .avatar("avatar " + i)
                    .chatSession(mockChatSession)
                    .build();
            profileRepository.save(mockProfile);
        }

        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON)
                .param("key", mockChatSession.getKey()))
                .andExpect(status().isServiceUnavailable())
                .andDo(print());
    }

    @DisplayName("No given parameters should return Bad Request")
    @Test
    void noParametersGivenShouldReturnBAD_REQUEST() throws Exception {
        mockMvc.perform(get("/api/meetings/find-meeting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(result -> assertThat(Objects.equals(result
                        .getResponse()
                        .getErrorMessage(), "No key value or chat id was provided.")));
    }


    @Test
    void chatSessionContainingOneProfileShouldReturnACollectionOfSize1() throws Exception {
        Profile mockProfile = MockData.getMockProfileEntity();
        mockProfile.setChatSession(mockChatSession);
        profileRepository.save(mockProfile);
        MvcResult mvcResult = mockMvc.perform(get("/api/meetings/profiles-connected")
                .param("chat-id", mockChatSession.getGuid().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        Profile[] profiles = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Profile[].class);

        assertThat(profiles.length).isEqualTo(1);
    }
}
