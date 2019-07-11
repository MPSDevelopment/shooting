package tech.shooting.commons.pojo;

import java.util.HashMap;
import java.util.Map;

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

    private static final String MESSAGE_KEY = "message";
    
	@JsonProperty
    private Map<String, String> error = new HashMap<>();
    
	public ErrorMessage(String message, Object... objects) {
		this.error.put(MESSAGE_KEY,  String.format(message, objects));
	}
	
	@Override
	public String toString() {
		return error.get(MESSAGE_KEY);
	}

}
