package InuCapstone.Server.application.mail;

import InuCapstone.Server.domain.key.AuthKey;
import InuCapstone.Server.domain.key.AuthKeyRepository;
import InuCapstone.Server.dto.mail.AuthRequestDTO;
import InuCapstone.Server.dto.mail.AuthResultResponseDTO;
import InuCapstone.Server.dto.mail.KeyRequestDTO;
import InuCapstone.Server.dto.mail.MailSendResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailService {

    private final AuthKeyRepository authKeyRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Transactional
    public String makeKey(String email) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 9; i++) {
            SecureRandom rnd = new SecureRandom();
            int index = rnd.nextInt(3);
            switch (index) {
                case 0 -> stringBuffer.append((char)rnd.nextInt('A', 'Z'));
                case 1 -> stringBuffer.append((char)rnd.nextInt('a', 'z'));
                case 2 -> stringBuffer.append(rnd.nextInt(0, 9));
            }
        }
        AuthKey authKey = AuthKey.builder()
                .authKey(stringBuffer.toString())
                .email(email)
                .createdTime(LocalDateTime.now())
                .build();
        authKeyRepository.save(authKey);
        return stringBuffer.toString();
    }

    private void sendMail(MailSendResponseDTO dto) {
        try {
            SimpleMailMessage message = dtoToMessage(dto);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleMailMessage dtoToMessage(MailSendResponseDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(dto.getEmail());
        message.setSubject(dto.getTitle());
        message.setText(dto.getMessage());
        return message;
    }

    @Transactional
    public void sendAuthKey(KeyRequestDTO dto){
        sendMail(writeEmailWithKey(dto.getEmail()));
    }

    @Transactional
    public MailSendResponseDTO writeEmailWithKey(String email){
        String key = makeKey(email);
        return MailSendResponseDTO.builder()
                .email(email)
                .title("인증번호 발송")
                .message("인증번호는 " + key + " 입니다.")
                .build();
    }

    public AuthResultResponseDTO authValidate(AuthRequestDTO dto) {
        List<AuthKey> authKeys = authKeyRepository.findAllByEmail(dto.getEmail());
        if (authKeys.isEmpty()) {
            //인증키가 없어요 오류??
            return AuthResultResponseDTO.builder()
                    .ok(false)
                    .build();
        }
        authKeys.sort((o1, o2) -> o2.getCreatedTime().isAfter(o1.getCreatedTime()) ? 1 : -1);
        return AuthResultResponseDTO.builder()
                .ok(authKeys.get(0).getAuthKey().equals(dto.getKey()))
                .build();
    }
}