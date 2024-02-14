package InuCapstone.Server.exception;

import InuCapstone.Server.common.CustomException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionMessage> handle(CustomException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(e.getException().getStatusCode()))
                .body(new ExceptionMessage(e.getException(),e.getMessage()));
    }
}
