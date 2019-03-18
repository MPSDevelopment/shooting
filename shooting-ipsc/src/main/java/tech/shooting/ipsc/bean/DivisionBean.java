package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "Division create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class DivisionBean {
	@JsonProperty
	@ApiModelProperty(value = "Parent name", required = true)
	private Long parent;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	@NotNull(message = ValidationConstants.DIVISION_NAME_MESSAGE)
	@Size(min = 3, max = 20, message = ValidationConstants.DIVISION_NAME_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "List of children")
	@DBRef
	private List<Division> children;

	@JsonProperty
	@ApiModelProperty(value = "Status division")
	private boolean active;
}
