package InuCapstone.Server.dto.user;

import InuCapstone.Server.domain.user.User;
import lombok.Getter;

@Getter
public class UserAddRequestDTO {

    private Long userId;
    private String email;
    private String password;
    private String phoneNumber;

    public User toEntity(){
        return User.builder()
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .build();
    }
}
