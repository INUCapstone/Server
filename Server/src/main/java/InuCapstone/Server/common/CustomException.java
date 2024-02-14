package InuCapstone.Server.common;

import InuCapstone.Server.exception.Exception;

public abstract class CustomException extends RuntimeException{

    public abstract Exception getException();

    public abstract String getMessage();

    public CustomException(String message){
        super(message);
    }
}
