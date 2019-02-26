package tech.shooting.ipsc.advice;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.InternalServerErrorException;
import tech.shooting.commons.exception.NotAcceptableException;
import tech.shooting.commons.exception.NotModifiedException;
import tech.shooting.commons.pojo.ErrorMessage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class RestErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage processBadRequestException(BadRequestException e) {
        log.error("Bad request %s", e.getErrorMessage());
        return e.getErrorMessage();
    }


    @ExceptionHandler(NotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage processNotAcceptableException(NotAcceptableException e) {
        log.error("Not acceptable %s", e.getErrorMessage());
        return e.getErrorMessage();
    }

    @ExceptionHandler(NotModifiedException.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    public ErrorMessage processNotModifiedException(NotModifiedException e) {
        return e.getErrorMessage();
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage processInternalServerErrorException(InternalServerErrorException e) {
        log.error("Internal server error %s", e.getErrorMessage());
        return e.getErrorMessage();
    }
}
