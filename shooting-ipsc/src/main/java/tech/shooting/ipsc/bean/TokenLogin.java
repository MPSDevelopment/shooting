package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TokenLogin {

	public static final String TOKEN_FIELD = "token";

	@JsonProperty(value = TOKEN_FIELD)
	private String token;

}
