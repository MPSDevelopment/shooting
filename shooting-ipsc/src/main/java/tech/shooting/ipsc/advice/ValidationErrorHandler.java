package tech.shooting.ipsc.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mongodb.MongoWriteException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.shooting.commons.exception.*;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.utils.ExceptionUtils;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ValidationErrorHandler {

	private static final String DEFAULT_FIELD = "message";
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage processValidationError(NotFoundException ex, HttpServletRequest request) {
		log.error("Not found error in request %s", request.getRequestURL());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, ex.getMessage());
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> processValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
		log.error("Validation Error in request %s", request.getRequestURL());
		BindingResult result = ex.getBindingResult();
		Map<String, String> validationErrors = processFieldErrors(result.getFieldErrors());
		log.error("Validation Errors: %s", validationErrors);
		return validationErrors;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processValidationException(HttpMessageNotReadableException ex, HttpServletRequest request) {
		log.error("Validation exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, ex.getMessage());
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processValidationException(ValidationException ex, HttpServletRequest request) {
		log.error("Validation exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(ex.getField(), ex.getMessage());
		return new ErrorMessage(validationErrors);
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage process(FileNotFoundException ex, HttpServletRequest request) {
		log.error("FileNotFound exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ErrorMessage(DEFAULT_FIELD, ex.getMessage());
	}

	@ExceptionHandler(UnexpectedTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processUnexpectedType(UnexpectedTypeException ex, HttpServletRequest request) {
		log.error("Validation Error in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ErrorMessage(DEFAULT_FIELD, ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processUnexpectedType(IllegalArgumentException ex, HttpServletRequest request) {
		log.error("IllegalArgument Error in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, ex.getMessage());
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processInvalidFormatException(InvalidFormatException ex, HttpServletRequest request) {
		log.error("Invalid Format Exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, ex.getMessage());
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processBadRequestException(BadRequestException e) {
		log.error("Bad request %s", e.getErrorMessage());
		return e.getErrorMessage();
	}
	
	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> processDuplicateKeyException(DuplicateKeyException ex, HttpServletRequest request) {
		log.error("Duplicate key exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		log.error("Duplicate key exception field %s value %s", ExceptionUtils.getDuplicateKeyErrorField(ex.getMessage()), ExceptionUtils.getDuplicateKeyErrorValue(ex.getMessage()));
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(ExceptionUtils.getDuplicateKeyErrorField(ex.getMessage()), ValidationConstants.DUPLICATE_KEY_MESSAGE + " : " + ExceptionUtils.getDuplicateKeyErrorValue(ex.getMessage()));
		return validationErrors;
	}
	
	@ExceptionHandler(MongoWriteException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage processValidationException(MongoWriteException ex, HttpServletRequest request) {
		log.error("Database exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, ex.getMessage());
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage processForbiddenException(ForbiddenException ex, HttpServletRequest request) {
		log.error("Forbidden exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ErrorMessage(DEFAULT_FIELD, "Forbidden exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage processForbiddenException(AccessDeniedException ex, HttpServletRequest request) {
		log.error("Access denied exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, String.format("Access denied in request %s", request.getRequestURL()));
		return new ErrorMessage(validationErrors);
	}

	@ExceptionHandler(NotAcceptableException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	public ErrorMessage processNotAcceptableException(NotAcceptableException e, HttpServletRequest request) {
		log.error("Not acceptable in request %s with error %s", request.getRequestURL(), e.getErrorMessage());
		return new ErrorMessage(DEFAULT_FIELD, "Not acceptable request %s", request.getRequestURL());
	}

	@ExceptionHandler(NotModifiedException.class)
	@ResponseStatus(HttpStatus.NOT_MODIFIED)
	public ErrorMessage processNotModifiedException(NotModifiedException e) {
		return e.getErrorMessage();
	}

	@ExceptionHandler(InternalServerErrorException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage processInternalServerErrorException(InternalServerErrorException e, HttpServletRequest request) {
		log.error("Internal server error in request %s with error %s", request.getRequestURL(), e.getErrorMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, e.getMessage());
		return new ErrorMessage(validationErrors);
	}
	
	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage processNullPointerException(InternalServerErrorException e, HttpServletRequest request) {
		log.error("Null pointer error in request %s with error %s", request.getRequestURL(), e.getErrorMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(DEFAULT_FIELD, e.getMessage());
		return new ErrorMessage(validationErrors);
	}


	private Map<String, String> processFieldErrors(List<FieldError> fieldErrors) {
		Map<String, String> validationErrors = new HashMap<>();
		for (FieldError fieldError : fieldErrors) {
			ValidationException exception = resolveValidationError(fieldError);
			String field = exception.getField();
			validationErrors.put(field, exception.getMessage());
		}
		return validationErrors;
	}

	private ValidationException resolveValidationError(FieldError fieldError) {
		return new ValidationException(fieldError.getField(), fieldError.getDefaultMessage());
	}
}
