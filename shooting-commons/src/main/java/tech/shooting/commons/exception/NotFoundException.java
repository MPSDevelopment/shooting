package tech.shooting.commons.exception;

import tech.shooting.commons.pojo.ErrorMessage;

public class NotFoundException extends RequestException {

	public NotFoundException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

}
