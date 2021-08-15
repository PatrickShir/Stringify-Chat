package se.nackademin.stringify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import se.nackademin.stringify.domain.Message;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "A message object to be published to users connected to a chat session")
public class MessageDto implements IConvertEntity<Message> {

    @ApiModelProperty(notes = "An id used by the client side")
    private UUID guid;

    @NotBlank(message = "Please provide from whom this message was sent from.")
    @Size(min = 3, max = 30)
    @ApiModelProperty(notes = "The name of the user who sent the message", example = "John Doe")
    private String from;

    @NotBlank(message = "Please provide the content of the message")
    @Size(min = 1, max = 1000, message = "The content of the message must be at least 1 characters long and maximum 1000.")
    @ApiModelProperty(notes = "The content of the message", example = "Hello there!", required = true)
    private String content;
    @ApiModelProperty(notes = "The avatar to be displayed on client-side. Avatars to be used are avatar1 - avatar20",
            example = "avatar1")
    private String avatar;
    @ApiModelProperty(notes = "The timestamp of the time of sending the message", example = "2021-01-01 14:32")
    private String date;

    /***
     * Converts data transfer object to related entity.
     * @return {@code Message.class}
     */
    @Override
    public Message convertToEntity() {
        return Message.builder()
                .guid(this.guid)
                .sender(this.from)
                .avatar(this.avatar)
                .content(this.content)
                .build();
    }
}
