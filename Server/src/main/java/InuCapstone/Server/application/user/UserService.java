package InuCapstone.Server.application.user;

import InuCapstone.Server.domain.user.User;
import InuCapstone.Server.domain.user.UserRepository;
import InuCapstone.Server.dto.user.UserSignUpRequestDTO;
import InuCapstone.Server.dto.user.UserReadResponseDTO;
import InuCapstone.Server.dto.user.UserUpdateRequestDTO;
import InuCapstone.Server.exception.CustomException;
import InuCapstone.Server.exception.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long signUp(UserSignUpRequestDTO dto){
        checkDuplicatedEmail(dto.getEmail());
        User user = dto.toEntity();
        userRepository.save(user);
        return user.getUserId();
    }

    private void checkDuplicatedEmail(String email){
        userRepository.findByEmail(email).ifPresent(a -> {
            throw new CustomException(Status.USER_DUPLICATED);
        });
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new CustomException(Status.USER_NOT_EXIST));
    }

    public UserReadResponseDTO readUser(Long userId){
        User user = findById(userId);
        return UserReadResponseDTO.toDTO(user);
    }

    @Transactional
    public void updateUser(UserUpdateRequestDTO dto){
        User user = findById(dto.getUserId());
        user.updateUser(dto);
    }

    @Transactional
    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
}
