package tech.shooting.commons.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.shooting.commons.pojo.ErrorMessage;

@NoArgsConstructor
@Getter
@Setter
public abstract class RequestException extends Exception {

	@JsonProperty
	protected ErrorMessage errorMessage;
    
    public RequestException(ErrorMessage errorMessage) {
    	this.errorMessage = errorMessage;
    }

}
