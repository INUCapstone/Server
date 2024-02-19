package InuCapstone.Server.dto.user;

import InuCapstone.Server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserReadResponseDTO {

    private Long userId;
    private String email;
    private String password;
    private String phoneNumber;

    public static UserReadResponseDTO toDTO(User user){
        return UserReadResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
