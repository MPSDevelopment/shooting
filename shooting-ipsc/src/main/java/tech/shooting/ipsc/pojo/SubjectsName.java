package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SubjectsName {
	@JsonProperty
	@ApiModelProperty("Constant name short")
	private String useName;

	@JsonProperty
	@ApiModelProperty("Full name")
	private String name;
}
