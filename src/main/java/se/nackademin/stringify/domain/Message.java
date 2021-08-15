package se.nackademin.stringify.domain;

import lombok.*;
import se.nackademin.stringify.dto.MessageDto;
import se.nackademin.stringify.util.DateUtil;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Builder
@Setter
@Entity
@Table(name = "message")
@NoArgsConstructor
@AllArgsConstructor
/***
 * Model for a single message containing id, guid, sender, content, avatar, date and a chat session.
 */
public class Message implements IConvertDto<MessageDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID guid = UUID.randomUUID();

    @NotBlank(message = "Sender cannot be empty!")
    @Size(min = 3, max = 30)
    private String sender;

    /*@Column(length = 1000, nullable = false)*/
    @NotBlank(message = "Content cannot be empty!")
    @Size(min = 1, max = 1000)
    private String content;
    private String avatar;
    private Timestamp date;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ChatSession chatSession;

    /**
     * Converts Entity to related data transfer object.
     * @return {@code MessageDto.class}
     */
    @Override
    public MessageDto convertToDto() {
        return MessageDto.builder()
                .guid(this.getGuid())
                .from(this.sender)
                .avatar(this.avatar)
                .date(DateUtil.dateToString(this.date))
                .content(this.content)
                .guid(this.getGuid())
                .build();
    }
}
