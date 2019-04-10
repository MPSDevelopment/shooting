package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Competitor added mark")
@EqualsAndHashCode(callSuper = false)
@ToString
public class CompetitorMarks {
	@JsonProperty("userName")
	@ApiModelProperty(value = "Competitor's name", required = true)
	@Size(min = 3, message = "Min characters  is 3")
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's rfid")
	private String rfid;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's number")
	private String number;

	@JsonProperty
	@ApiModelProperty(value = "Competitor's active, if he passed all the docs and other checks", required = true)
	@AssertTrue
	private boolean active;
}
