package tech.shooting.commons.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class SuccessfulMessage {

	@JsonProperty
	private String message;

	public SuccessfulMessage(String message, Object... objects) {
		this.message = String.format(message, objects);
	}

}
