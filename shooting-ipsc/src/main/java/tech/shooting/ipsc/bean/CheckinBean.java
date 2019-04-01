package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.pojo.TypePresent;

import javax.validation.constraints.NotNull;

@Data
public class CheckinBean {
	@JsonProperty
	@ApiModelProperty(value = "Person id from db")
	@NotNull
	private Long person;

	@JsonProperty
	@ApiModelProperty(value = "Status is present")
	private TypePresent status;
}
