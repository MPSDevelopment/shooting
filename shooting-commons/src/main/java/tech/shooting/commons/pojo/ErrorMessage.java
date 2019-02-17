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
public class ErrorMessage {

    @JsonProperty
    private String message;
    
	public ErrorMessage(String message, Object... objects) {
		this.message = String.format(message, objects);
	}
	
	@Override
	public String toString() {
		return message;
	}

}
