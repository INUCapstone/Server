package InuCapstone.Server.dto.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailSendResponseDTO {

    private String email;
    private String title;
    private String message;
}