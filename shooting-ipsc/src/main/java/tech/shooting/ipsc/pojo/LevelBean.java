package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;
import tech.shooting.ipsc.enums.ClassificationBreaks;

import javax.validation.constraints.PositiveOrZero;

@Accessors(chain = true)
@Data
public class LevelBean implements ValiationExportable {
	@JsonProperty
	@ApiModelProperty(value = "Level description", required = true)
	private String description;

	@JsonProperty()
	@ApiModelProperty(value = "Level mark", required = true)
	private ClassificationBreaks classificationBreaks;

	@JsonProperty()
	@ApiModelProperty(value = "min range for this mark", required = true)
	@PositiveOrZero
	private Float min;

	@JsonProperty()
	@PositiveOrZero
	@ApiModelProperty(value = "max range for this mark", required = true)
	private Float max;
}
