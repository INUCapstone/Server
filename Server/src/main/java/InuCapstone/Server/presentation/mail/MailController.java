package InuCapstone.Server.presentation.mail;

import InuCapstone.Server.application.mail.MailService;
import InuCapstone.Server.dto.mail.AuthRequestDTO;
import InuCapstone.Server.dto.mail.KeyRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendAuthKey(@RequestBody KeyRequestDTO dto){
        mailService.sendAuthKey(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authMail(@RequestBody AuthRequestDTO dto){
        return ResponseEntity.ok().body(mailService.authValidate(dto));
    }
}
