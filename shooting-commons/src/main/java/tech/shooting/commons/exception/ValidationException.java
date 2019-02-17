package tech.shooting.commons.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 8840966802614690464L;

	@JsonProperty
	private String field;
	@JsonProperty
	private String message;

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String field, String message, Object... objects) {
		this.field = field;
		this.message = String.format(message, objects);
	}

	@Override
	public String toString() {
		return String.format("Field %s message %s ", field, message);
	}

}
