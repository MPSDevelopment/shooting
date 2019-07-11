package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "User signup")
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserSignupBean {
	public static final String LOGIN_FIELD = "login";

	public static final String PASSWORD_FIELD = "password";

	public static final String NAME_FIELD = "name";

	public static final String BIRTHDATE_FIELD = "birthDate";

	@JsonProperty
	@ApiModelProperty(value = "User's login", required = true)
	@NotEmpty(message = ValidationConstants.USER_INCORRECT_LOGIN_MESSAGE)
	@Size(min = 3, max = 50, message = ValidationConstants.USER_LOGIN_MESSAGE)
	private String login;

	@JsonProperty
	@ApiModelProperty(value = "User's password", required = true)
	@NotEmpty(message = ValidationConstants.USER_PASSWORD_MESSAGE)
	@Size(min = 5, message = ValidationConstants.USER_PASSWORD_MESSAGE)
	private String password;

	@JsonProperty("userName")
	@ApiModelProperty(value = "User's name")
	@NotNull(message = ValidationConstants.USER_NAME_MESSAGE)
	@Size(min = 3, max = 50, message = ValidationConstants.USER_NAME_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "User's birthday")
	private OffsetDateTime birthDate;
}
