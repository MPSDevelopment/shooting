package tech.shooting.ipsc.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.validator.ValidationConstants;

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
	
	
}
