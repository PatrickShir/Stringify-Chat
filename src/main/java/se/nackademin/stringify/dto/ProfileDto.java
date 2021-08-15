package se.nackademin.stringify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import se.nackademin.stringify.domain.Profile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "A object used for a client to be identified with during a chat session")
public class ProfileDto implements IConvertEntity<Profile> {

    @ApiModelProperty(notes = "An id used by the client side")
    private UUID guid;
    @NotBlank(message = "Please provide the name of the profile")
    @Size(min = 3, max = 30, message = "Name has to be at least 3 characters long and max 30 characters long.")
    @ApiModelProperty(required = true, notes = "The name of the profile user", example = "Jake Fish")
    private String name;
    @ApiModelProperty(notes = "A avatar used by the client-side", example = "avatar20")
    private String avatar;

    /***
     * Converts data transfer object to related entity.
     * @return {@code Profile.class}
     */
    @Override
    public Profile convertToEntity() {
        return Profile.builder()
                .guid(this.guid)
                .name(this.name)
                .avatar(this.avatar)
                .build();
    }
}
