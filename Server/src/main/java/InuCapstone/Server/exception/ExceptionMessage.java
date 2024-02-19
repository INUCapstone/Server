package InuCapstone.Server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionMessage {

    private Status status;
    private String message;
}
