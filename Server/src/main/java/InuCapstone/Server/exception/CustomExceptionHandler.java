package InuCapstone.Server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionMessage> handle(CustomException e){
        return ResponseEntity.status(e.getStatus().getStatusCode())
                .body(new ExceptionMessage(e.getStatus(),e.getStatus().getMessage()));
    }
}
