package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@Document(collection = "stage")
@TypeAlias("stage")
public class Stage extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Name or № of stage", required = true)
	@NotNull(message = ValidationConstants.STAGE_NAME_MESSAGE)
	@Size(min = 5, max = 50, message = ValidationConstants.STAGE_NAME_MESSAGE)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC target", required = true)
	@NotNull(message = ValidationConstants.STAGE_TARGETS_COUNT_MESSAGE)
	@Min(value = 1, message = ValidationConstants.STAGE_TARGETS_COUNT_MESSAGE)
	private Integer targets;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC popper")
	private Integer popper;

	@JsonProperty
	@ApiModelProperty(value = "Count of IPSC no shoots target")
	private Integer noShoots;

	@JsonProperty
	@ApiModelProperty(value = "Count of round", required = true)
	@NotNull(message = ValidationConstants.STAGE_ROUND_COUNT_MESSAGE)
	@Min(value = 1, message = ValidationConstants.STAGE_ROUND_COUNT_MESSAGE)
	private Integer numberOfRoundToBeScored;

	@JsonProperty
	@ApiModelProperty(value = "Count of max points for the stage", required = true)
	@NotNull(message = ValidationConstants.STAGE_STAGE_MAXIMUM_POINTS_MESSAGE)
	@Min(value = 5, message = ValidationConstants.STAGE_STAGE_MAXIMUM_POINTS_MESSAGE)
	private Integer maximumPoints;

	@JsonProperty
	@ApiModelProperty(value = "Description of start position")
	private String startPosition;

	@JsonProperty
	@ApiModelProperty(value = "Description of ready condition")
	private String readyCondition;

	@JsonProperty
	@ApiModelProperty(value = "Description of procedure")
	private String procedure;

	@JsonProperty
	@ApiModelProperty(value = "Counted stage or not")
	private boolean notCounted;

	@JsonProperty
	@ApiModelProperty(value = "Name of fire position")
	private String location;


}
