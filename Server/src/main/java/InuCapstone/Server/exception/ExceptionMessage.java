package InuCapstone.Server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionMessage {

    private Exception exception;
    private String message;
}
