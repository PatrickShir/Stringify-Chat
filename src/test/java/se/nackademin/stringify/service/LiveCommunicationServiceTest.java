package se.nackademin.stringify.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.nackademin.stringify.MockData;
import se.nackademin.stringify.controller.response.ConnectionNotice;
import se.nackademin.stringify.domain.ChatSession;
import se.nackademin.stringify.domain.Message;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.exception.ProfileNotFoundException;
import se.nackademin.stringify.repository.ChatSessionRepository;
import se.nackademin.stringify.repository.MessageRepository;
import se.nackademin.stringify.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class LiveCommunicationServiceTest {

    @InjectMocks
    LiveCommunicationService liveCommunicationService;

    @Mock
    MessageRepository messageRepository;

    @Mock
    ChatSessionRepository chatSessionRepository;

    @Mock
    ProfileRepository profileRepository;

    @Test
    void nonExistingChatSessionShouldThrowChatSessionNotFoundException() {
        given(chatSessionRepository.findByGuid(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> liveCommunicationService.getChatSession(UUID.randomUUID()));
    }

    @Test
    void testStoreMessageShouldSaveMessageWithActiveChatSession() {
        Message mockedMessage = MockData.getMockMessageEntity();
        given(chatSessionRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(MockData.getMockChatSessionEntity()));
        given(messageRepository.save(any(Message.class))).willReturn(mockedMessage);

        mockedMessage.setId(null);
        Message message = liveCommunicationService.storeMessage(UUID.randomUUID(), mockedMessage);
        then(chatSessionRepository).should().findByGuid(any(UUID.class));

        assertThat(message.getId()).isNotNull();
        assertThat(message.getChatSession()).isNotNull();
    }

    @Test
    void existingProfileShouldNotBeSavedAgain() {
        given(chatSessionRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(MockData.getMockChatSessionEntity()));
        Profile mockProfile = MockData.getMockProfileEntity();
        given(profileRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(mockProfile));
        given(messageRepository.save(any())).willReturn(MockData.getMockMessageEntity());

       liveCommunicationService.
                storeProfileConnected(UUID.randomUUID(), mockProfile);

        then(profileRepository).should(times(0)).save(any());
    }

    @Test
    void nonExistingProfileShouldBeSaved() {
        given(chatSessionRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(MockData.getMockChatSessionEntity()));
        Profile mockProfile = MockData.getMockProfileEntity();
        given(profileRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.empty());
        given(profileRepository.save(any(Profile.class)))
                .willReturn(mockProfile);
        given(messageRepository.save(any())).willReturn(MockData.getMockMessageEntity());
        liveCommunicationService.
                storeProfileConnected(UUID.randomUUID(), mockProfile);

        then(profileRepository).should(times(1)).save(any());
    }

    @Test
    void nonExistingProfileShouldThrowProfileNotFoundException() {
        given(profileRepository.findAllByGuidAndChatSession_Guid(any(), any()))
                .willThrow(ProfileNotFoundException.class);

        assertThatThrownBy(() -> liveCommunicationService.removeProfileDisconnected(UUID.randomUUID(), new Profile()));
    }

    @Test
    void chatSessionWithMoreThanOneConnectedProfileShouldReturnConnectionNotice() {
        ChatSession mockChatSession = MockData.getMockChatSessionEntity();
        Profile mockProfile = MockData.getMockProfileEntity();
        List<Profile> mockProfiles = new ArrayList<>();
        mockProfiles.add(mockProfile);
        mockProfiles.add(new Profile());
        mockChatSession.setProfilesConnected(mockProfiles);

        given(profileRepository.findAllByGuidAndChatSession_Guid(any(), any()))
                .willReturn(Optional.of(mockProfile));
        given(chatSessionRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(mockChatSession));
        given(messageRepository.save(any())).willReturn(MockData.getMockMessageEntity());

        ConnectionNotice connectionNotice = liveCommunicationService
                .removeProfileDisconnected(UUID.randomUUID(), MockData.getMockProfileEntity());
        then(profileRepository).should(times(1)).deleteById(any());
        then(chatSessionRepository).should(times(0)).deleteById(any());
        then(messageRepository).should(times(1)).save(any());

        assertThat(connectionNotice).isNotNull();
    }

    @Test
    void chatSessionWithOneProfileConnectedShouldBeDeletedOnDisconnectAndReturnNull() {
        ChatSession mockChatSession = MockData.getMockChatSessionEntity();
        Profile mockProfile = MockData.getMockProfileEntity();
        List<Profile> mockProfiles = new ArrayList<>();
        mockProfiles.add(mockProfile);
        mockChatSession.setProfilesConnected(mockProfiles);

        given(profileRepository.findAllByGuidAndChatSession_Guid(any(), any()))
                .willReturn(Optional.of(mockProfile));
        given(chatSessionRepository.findByGuid(any(UUID.class)))
                .willReturn(Optional.of(mockChatSession));

        ConnectionNotice connectionNotice = liveCommunicationService
                .removeProfileDisconnected(UUID.randomUUID(), MockData.getMockProfileEntity());
        then(profileRepository).should(times(1)).deleteById(any());
        then(chatSessionRepository).should(times(1)).deleteById(any());
        then(messageRepository).should(times(0)).save(any());

        assertThat(connectionNotice).isNull();
    }
}
