package InuCapstone.Server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Status {

    //409 : 리소스 충돌
    USER_DUPLICATED(HttpStatus.CONFLICT,"해당 이메일은 이미 사용중입니다."),
    //401 : 비인증 사용자
    USER_NOT_EXIST(HttpStatus.UNAUTHORIZED,"존재하지 않는 유저입니다.");
    private final HttpStatus statusCode;
    private final String message;
}
