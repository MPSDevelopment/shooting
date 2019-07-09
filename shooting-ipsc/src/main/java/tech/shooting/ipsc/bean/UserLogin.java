package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.EnablePasswordConstraint;
import tech.shooting.ipsc.validator.LoginForValidPassword;
import tech.shooting.ipsc.validator.ValidPassword;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
//@EnablePasswordConstraint
public class UserLogin {
	@JsonProperty
	@ApiModelProperty(value = "User's login", required = true)
	@NotBlank(message = ValidationConstants.USER_LOGIN_REQUIRED_MESSAGE)
//	@LoginForValidPassword
	private String login;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ApiModelProperty(value = "User's password", required = true)
	@NotBlank(message = ValidationConstants.USER_PASSWORD_REQUIRED_MESSAGE)
	@Size(min = 4, message = ValidationConstants.USER_PASSWORD_MESSAGE)
//	@ValidPassword(message = ValidationConstants.LOGIN_INCORRECT)
	private String password;
}
