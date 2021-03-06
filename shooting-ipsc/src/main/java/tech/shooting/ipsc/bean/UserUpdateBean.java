package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;

@Data
public class UserUpdateBean {
	
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(value = "User's id")
	private long id;

	@JsonProperty("userName")
	@ApiModelProperty(value = "User's name")
	@NotNull(message = ValidationConstants.USER_NAME_MESSAGE)
//	@Size(min = 5, max = 50, message = ValidationConstants.USER_NAME_MESSAGE)
	@Pattern(regexp = ValidationConstants.NAME_PATTERN, message = ValidationConstants.NAME_ONLY_DIGITS_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "User's address")
	private Address address;

	@JsonProperty
	@ApiModelProperty(value = "User's login")
	@NotEmpty(message = ValidationConstants.USER_INCORRECT_LOGIN_MESSAGE)
	private String login;

	@JsonProperty
	@ApiModelProperty(value = "User's birthday")
	@NotNull(message = ValidationConstants.USER_BIRTHDAY_MESSAGE)
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "User's active ")
	private boolean active;
}
