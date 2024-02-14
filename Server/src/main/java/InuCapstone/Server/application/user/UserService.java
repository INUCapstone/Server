package InuCapstone.Server.application.user;

import InuCapstone.Server.domain.user.User;
import InuCapstone.Server.domain.user.UserRepository;
import InuCapstone.Server.dto.user.UserAddRequestDTO;
import InuCapstone.Server.dto.user.UserFindResponseDTO;
import InuCapstone.Server.dto.user.UserUpdateRequestDTO;
import InuCapstone.Server.exception.user.DuplicatedUserEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long addUser(UserAddRequestDTO dto){
        checkDuplicatedEmail(dto.getEmail());
        User user = dto.toEntity();
        userRepository.save(user);
        return user.getUserId();
    }

    private void checkDuplicatedEmail(String email){
        userRepository.findByEmail(email).ifPresent(a -> {
            throw new DuplicatedUserEmailException();
        });
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }

    public UserFindResponseDTO findUser(Long userId){
        User user = findById(userId);
        return UserFindResponseDTO.toDTO(user);
    }

    @Transactional
    public void updateUser(UserUpdateRequestDTO dto){
        User user = findById(dto.getUserId());
        if(user==null) throw new IllegalStateException("존재하지 않는 유저입니다.");
        user.updateUser(dto);
    }

    @Transactional
    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
}
