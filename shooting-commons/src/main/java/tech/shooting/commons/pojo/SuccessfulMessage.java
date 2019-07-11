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
public class SuccessfulMessage {

    private static final String MESSAGE_KEY = "message";
    
	@JsonProperty
    private Map<String, String> success = new HashMap<>();

	public SuccessfulMessage(String message, Object... objects) {
		this.success.put(MESSAGE_KEY,  String.format(message, objects));
	}
	
	@Override
	public String toString() {
		return success.get(MESSAGE_KEY);
	}

}
