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
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 8840966802614690467L;

    public ForbiddenException(String field, String message, Object... objects) {
        this.field = field;
        this.message = String.format(message, objects);
    }

    @JsonProperty
    private String field;
    @JsonProperty
    private String message;

    @Override
    public String toString() {
        return String.format("Field %s %s ", field, message);
    }


}
