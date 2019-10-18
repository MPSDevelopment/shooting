package tech.shooting.ipsc.bean;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class StandardScoreRequest {

	@JsonProperty
	@ApiModelProperty(value = "Subject id")
	private Long subjectId;
	
	@JsonProperty
	@ApiModelProperty(value = "Standard id")
	private Long standardId;
	
	@JsonProperty
	@ApiModelProperty(value = "Person id")
	private Long personId;
	
	@JsonProperty
	@ApiModelProperty(value = "Start date")
	private OffsetDateTime startDate;
	
	@JsonProperty
	@ApiModelProperty(value = "End date")
	private OffsetDateTime endDate;

}
