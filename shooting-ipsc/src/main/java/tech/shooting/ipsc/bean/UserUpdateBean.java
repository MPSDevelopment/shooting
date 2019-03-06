package tech.shooting.ipsc.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.validator.ValidationConstants;

import java.time.OffsetDateTime;

@Data
public class UserUpdateBean {

	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(value = "User's id")
	private long id;

	@JsonProperty
	@ApiModelProperty(value = "User's name")
	@NotNull(message = ValidationConstants.USER_NAME_MESSAGE)
	@Size(min = 3, max = 50, message = ValidationConstants.USER_NAME_MESSAGE)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "User's address")
	private Address address;

	@JsonProperty
	@ApiModelProperty(value = "User's login")
	@NotEmpty(message = ValidationConstants.USER_LOGIN_MESSAGE)
	private String login;

	@JsonProperty
	@ApiModelProperty(value = "User's password")
	@NotEmpty(message = ValidationConstants.USER_PASSWORD_MESSAGE)
	@Size(min = 4, message = ValidationConstants.USER_PASSWORD_MESSAGE)
	private String password;

	@JsonProperty
	@ApiModelProperty(value = "User's birthday")
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "User's active ")
	private boolean active;
	
	
}