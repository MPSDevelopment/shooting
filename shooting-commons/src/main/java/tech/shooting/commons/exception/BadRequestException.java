package tech.shooting.commons.exception;

import lombok.NoArgsConstructor;
import tech.shooting.commons.pojo.ErrorMessage;

@NoArgsConstructor
public class BadRequestException extends RequestException {

    public BadRequestException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

}
