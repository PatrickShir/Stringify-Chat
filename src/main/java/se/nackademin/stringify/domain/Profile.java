package se.nackademin.stringify.domain;

import lombok.*;
import se.nackademin.stringify.dto.ProfileDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "profile")
@Builder
@AllArgsConstructor
@NoArgsConstructor
/***
 * Model for a single profile containing id, guid, name, avatar and a chat session.
 */
public class Profile implements IConvertDto<ProfileDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID guid = UUID.randomUUID();
    @NotBlank(message = "Please provide the name of the profile")
    @Size(min = 3, max = 30)
    private String name;
    private String avatar;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ChatSession chatSession;

    /***
     * Converts Entity to related data transfer object.
     * @return {@code ProfileDto.class}
     */
    @Override
    public ProfileDto convertToDto() {
        return ProfileDto.builder()
                .guid(getGuid())
                .name(this.name)
                .avatar(this.avatar)
                .build();
    }
}
