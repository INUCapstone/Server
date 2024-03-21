package InuCapstone.Server.dto.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    private String email;
    private String key;
}
