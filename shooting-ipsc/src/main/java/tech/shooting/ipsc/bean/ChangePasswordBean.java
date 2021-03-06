package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.EnablePasswordConstraint;
import tech.shooting.ipsc.validator.UserIdForValidPassword;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EnablePasswordConstraint
public class ChangePasswordBean {
	
	public static final String NEW_PASSWORD = "newPassword";

	private static final String USER_ID = "id";

	@JsonProperty(USER_ID)
	@UserIdForValidPassword
	@NotNull(message = ValidationConstants.USER_ID_MESSAGE)
	private Long id;

	@JsonProperty(value = NEW_PASSWORD)
	@NotBlank(message = ValidationConstants.USER_PASSWORD_MESSAGE)
	@Size(min = 4, message = ValidationConstants.USER_PASSWORD_MESSAGE)
	private String newPassword;
}
