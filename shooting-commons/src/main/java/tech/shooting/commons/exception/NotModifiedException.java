package tech.shooting.commons.exception;

import tech.shooting.commons.pojo.ErrorMessage;

public class NotModifiedException extends RequestException {

	public NotModifiedException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

}
