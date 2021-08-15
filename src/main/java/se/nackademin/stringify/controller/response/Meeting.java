package se.nackademin.stringify.controller.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.nackademin.stringify.dto.ChatSessionDto;
import se.nackademin.stringify.dto.ProfileDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "A response object for a user providing a profile when requesting to start a new meeting")
public class Meeting {

    private ProfileDto profile;
    private ChatSessionDto chatSession;
}
