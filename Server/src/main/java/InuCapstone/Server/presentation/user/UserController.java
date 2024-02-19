package InuCapstone.Server.presentation.user;

import InuCapstone.Server.application.user.UserService;
import InuCapstone.Server.dto.user.UserSignUpRequestDTO;
import InuCapstone.Server.dto.user.UserUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody UserSignUpRequestDTO dto){
        userService.signUp(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> readUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(userService.readUser(userId));
    }

    @PatchMapping("/modify")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequestDTO dto){
        userService.updateUser(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
