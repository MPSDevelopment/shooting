package tech.shooting.ipsc.bean;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.EnablePasswordConstraint;
import tech.shooting.ipsc.validator.LoginForValidPassword;
import tech.shooting.ipsc.validator.ValidPassword;
import tech.shooting.ipsc.validator.ValidationConstants;

@Data
@Accessors(chain = true)
@EnablePasswordConstraint
public class UserLogin {
	
	@JsonProperty
	@ApiModelProperty(value = "User's login", required = true)
	@NotBlank(message = ValidationConstants.USER_EMAIL_REQUIRED_MESSAGE)
	@Email(message = ValidationConstants.USER_EMAIL_MESSAGE)
	@LoginForValidPassword
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ApiModelProperty(value = "User's password", required = true)
	@NotBlank(message = ValidationConstants.USER_PASSWORD_REQUIRED_MESSAGE)
	@Size(min = 5, message = ValidationConstants.USER_PASSWORD_MESSAGE)
	@ValidPassword(message = ValidationConstants.LOGIN_INCORRECT)
	private String password;

}
