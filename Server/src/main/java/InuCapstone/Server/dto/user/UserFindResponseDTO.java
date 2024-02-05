package InuCapstone.Server.dto.user;

import InuCapstone.Server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserFindResponseDTO {

    private Long userId;
    private String email;
    private String password;
    private String phoneNumber;

    public static UserFindResponseDTO toDTO(User user){
        return UserFindResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
