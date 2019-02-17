package tech.shooting.commons.exception;

import lombok.NoArgsConstructor;
import tech.shooting.commons.pojo.ErrorMessage;

@NoArgsConstructor
public class NotAcceptableException extends RequestException {

	public NotAcceptableException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

}
