package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Division create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class DivisionBean {
	@JsonProperty
	@ApiModelProperty(value = "Parent name", required = true)
	@DBRef
	private Division parent;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	@NotNull(message = ValidationConstants.DIVISION_NAME_MESSAGE)
	@Size(min = 3, max = 20, message = ValidationConstants.DIVISION_NAME_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "List of children", required = true)
	@DBRef
	private List<Division> children;

	@JsonProperty
	@ApiModelProperty(value = "Status division", required = true)
	private boolean active;
}
