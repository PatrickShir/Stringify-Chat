package se.nackademin.stringify.controller.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import se.nackademin.stringify.dto.MessageDto;
import se.nackademin.stringify.dto.ProfileDto;

@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "A response object for connection notifications, containing a profile and a message")
public class ConnectionNotice {

    private ProfileDto profile;
    private MessageDto connectionMessage;

}
