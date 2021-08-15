package se.nackademin.stringify.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import se.nackademin.stringify.controller.response.ConnectionNotice;
import se.nackademin.stringify.dto.MessageDto;
import se.nackademin.stringify.dto.ProfileDto;
import se.nackademin.stringify.service.LiveCommunicationService;

import javax.validation.Valid;
import java.util.UUID;

/**
 * A controller class to handle the websocket connection to a chatSession
 */
@Controller
@RequiredArgsConstructor
public class LiveCommunicationController {

    private final LiveCommunicationService liveCommunicationService;

    /**
     * Receives and transmits messages between clients who connected.
     *
     * @param chatSessionGuid DESTINATION: The specific chat session ID to connect to.
     * @param messageDto      The message sent from the a client connected to session.
     * @return {@code MessageDto.class}
     */
    @MessageMapping("/send/meeting/{chatSessionGuid}")
    @SendTo("/queue/meeting/{chatSessionGuid}")
    public MessageDto transmit(@DestinationVariable UUID chatSessionGuid,
                               @Payload @Valid MessageDto messageDto) {

        return liveCommunicationService.storeMessage(chatSessionGuid, messageDto.convertToEntity())
                .convertToDto();
    }

    /**
     * Whenever a client connects to a session a connection notice will be received and published.
     *
     * @param chatSessionGuid DESTINATION: The specific chat session ID to connect to.
     * @param profile         The profile created by the client.
     * @return {@code ConnectionNotice.class} - Contains a profile and a message.
     */
    @MessageMapping("/connect/{chatSessionGuid}")
    @SendTo("/queue/connect/{chatSessionGuid}")
    public ConnectionNotice notifyOnConnect(@DestinationVariable UUID chatSessionGuid,
                                            @Payload @Valid ProfileDto profile) {
        return liveCommunicationService.storeProfileConnected(chatSessionGuid,
                profile.convertToEntity());
    }

    /**
     * Whenever a client disconnects from a session a connection notice will be received and published.
     *
     * @param chatSessionGuid DESTINATION: The specific chat session ID to connect to.
     * @param profile         The profile created by the client.
     * @return {@code ConnectionNotice.class} - Contains a profile and a message.
     */
    @MessageMapping("/disconnect/{chatSessionGuid}")
    @SendTo("/queue/disconnect/{chatSessionGuid}")
    public ConnectionNotice notifyOnDisconnect(@DestinationVariable UUID chatSessionGuid,
                                               @Payload @Valid ProfileDto profile) {
        return liveCommunicationService.removeProfileDisconnected(chatSessionGuid,
                profile.convertToEntity());
    }

}
