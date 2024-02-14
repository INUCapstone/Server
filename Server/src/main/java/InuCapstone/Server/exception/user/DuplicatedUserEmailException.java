package InuCapstone.Server.exception.user;

import InuCapstone.Server.common.CustomException;
import InuCapstone.Server.exception.Exception;

public class DuplicatedUserEmailException extends CustomException {

    private final Exception exception;

    private static final String message = "해당 이메일은 이미 사용중입니다.";

    public DuplicatedUserEmailException() {
        super(message);
        this.exception = Exception.USER_DUPLICATED;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
