package tech.shooting.commons.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.enums.RoleName;

@Getter
@Setter
public class Token {

	public enum TokenType {
		USER, LOST_PASSWORD
	};

	public static final String FIELD_ID = "id";

	public static final String FIELD_LOGIN = "uid";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_ROLE = "role";

	public static final String COOKIE_KEY_FIELD = "accessToken";

	public static final String TOKEN_HEADER = "Authorization";

	public static final String COOKIE_DEFAULT_VALUE = "";
	
	@JsonProperty
	private Long id;

	@JsonProperty
	private String uid;

	@JsonProperty
	private TokenType type;

	@JsonProperty
	private RoleName role;

}
