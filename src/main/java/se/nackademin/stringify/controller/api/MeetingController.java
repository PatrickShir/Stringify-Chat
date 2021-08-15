package se.nackademin.stringify.controller.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.nackademin.stringify.controller.response.Meeting;
import se.nackademin.stringify.domain.Profile;
import se.nackademin.stringify.dto.ChatSessionDto;
import se.nackademin.stringify.dto.ProfileDto;
import se.nackademin.stringify.service.EmailService;
import se.nackademin.stringify.service.MeetingService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/***
 * Rest controller for everything to do with meetings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/meetings")
@CrossOrigin(origins = {"http://localhost:3000", "https://stringify-chat.netlify.app"})
public class MeetingController {

    private final MeetingService meetingService;
    private final EmailService emailService;

    @ApiOperation(
            value = "Stores a new chat session with the connected profile.",
            produces = "application/json",
            consumes = "application/json",
            notes = "Provide a profile to store a new chat session with the given profile in order " +
                    "to connect to the meeting.",
            response = Meeting.class
    )
    @ApiResponse(code = 200, message = "A new meeting has been created with given profile")
    @PostMapping("new-meeting")
    public Meeting newMeeting(@RequestBody @Valid ProfileDto profile) {
        return meetingService.createNewMeeting(profile.convertToEntity());
    }

    @ApiOperation(
            value = "Finds a chat session by Key or Chat id",
            produces = "application/json",
            consumes = "application/json",
            notes = "Provide either a key or a chat id in order to get information about the chat session",
            response = ChatSessionDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK: A chat session with the given key or chat id has been found.",
                    response = ChatSessionDto.class),
            @ApiResponse(code = 404, message = "Not found: A chat session with the given key or chat id could not be found."),
            @ApiResponse(code = 400, message = "Bad request: Invalid key or chat id."),
            @ApiResponse(code = 204, message = "No content: A chat session with the given key or chat id has been found but " +
                    "could not provide with information due to a connection limit.")
    })
    @GetMapping("find-meeting")
    public ChatSessionDto findMeeting(@RequestParam(required = false) String key,
                                      @RequestParam(required = false, name = "chat-id") UUID chatId) {
        final String NO_PARAM_FOUND = "No key value or chat id was provided.";

        if (key == null && chatId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NO_PARAM_FOUND);
        }

        if (key != null) {
            return meetingService.getChatSessionByKey(key).convertToDto();
        }

        return meetingService.getMeetingByGuid(chatId).convertToDto();
    }

    @ApiOperation(
            value = "Sends a invitation link by email.",
            consumes = "application/json",
            notes = "Calls Sendgrid API to send an email to the given email" +
                    " address with an invitation link to a active chat session.",
            response = void.class
    )
    @PostMapping("invite/{email}/by/{name}")
    public void newEmail(@Email @PathVariable String email,
                         @PathVariable("name") String profileName,
                         @RequestParam("chat-id") UUID chatId) {
        emailService.sendInvitationEmail
                (email, profileName, chatId);
    }

    @ApiOperation(
            value = "Fetches all connected profiles by chat id.",
            consumes = "application/json",
            produces = "application/json",
            notes = "Fetches all connected profiles to a active chat session by the given chat id",
            response = ProfileDto[].class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK: A chat session with the given key or chat id has been found.",
                    response = ProfileDto[].class),
            @ApiResponse(code = 404, message = "Not found: A chat session with the given key or chat id could not be found."),
    })
    @GetMapping("profiles-connected")
    public List<ProfileDto> fetchProfilesConnect(@RequestParam(name = "chat-id") UUID chatId) {
        return meetingService.getProfilesConnected(chatId).stream()
                .map(Profile::convertToDto)
                .collect(Collectors.toList());
    }
}
