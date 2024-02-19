package InuCapstone.Server.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final Status status;

    public CustomException(Status status){
        this.status=status;
    }
}
