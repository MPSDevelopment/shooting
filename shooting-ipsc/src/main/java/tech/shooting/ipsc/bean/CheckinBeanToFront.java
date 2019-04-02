package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.TypeOfPresence;

@Data
@Accessors(chain = true)
@ToString
public class CheckinBeanToFront extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "Person id from db")
	private Long person;

	@JsonProperty
	@ApiModelProperty(value = "Status is present")
	private TypeOfPresence status;

	@JsonProperty
	@ApiModelProperty(value = "Inspection officer")
	private Long officer;
}
