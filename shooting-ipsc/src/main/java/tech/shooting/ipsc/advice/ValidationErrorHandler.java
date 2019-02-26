package tech.shooting.ipsc.advice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

@RestControllerAdvice
@Slf4j
public class ValidationErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> processValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
		log.error("Validation Error in request %s", request.getRequestURL());
		BindingResult result = ex.getBindingResult();
		Map<String, String> validationErrors = processFieldErrors(result.getFieldErrors());
		log.error("Validation Errors: %s", validationErrors);
		return validationErrors;
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> processValidationException(ValidationException ex, HttpServletRequest request) {
		log.error("Validation exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		Map<String, String> validationErrors = new HashMap<>();
		validationErrors.put(ex.getField(), ex.getMessage());
		return validationErrors;
	}

	@ExceptionHandler(UnexpectedTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationException processUnexpectedType(UnexpectedTypeException ex, HttpServletRequest request) {
		log.error("Validation Error in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ValidationException("", ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationException processUnexpectedType(IllegalArgumentException ex, HttpServletRequest request) {
		log.error("IllegalArgument Error in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ValidationException("", ex.getMessage());
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationException processInvalidFormatException(InvalidFormatException ex, HttpServletRequest request) {
		log.error("Invalid Format Exception in request %s with error %s", request.getRequestURL(), ex.getMessage());
		return new ValidationException("", ex.getMessage());
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
