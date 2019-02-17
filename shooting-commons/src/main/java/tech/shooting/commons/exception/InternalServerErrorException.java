package tech.shooting.commons.exception;

import tech.shooting.commons.pojo.ErrorMessage;

public class InternalServerErrorException extends RequestException {

    public InternalServerErrorException() {
        super(new ErrorMessage("Internal error occurred"));
    }

    public InternalServerErrorException(ErrorMessage errorMessage) {
        super(errorMessage);
    }


}
