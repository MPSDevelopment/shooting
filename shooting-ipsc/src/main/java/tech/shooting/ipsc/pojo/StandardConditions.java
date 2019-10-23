package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.UnitEnum;

import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class StandardConditions {

	public static final String UNIT = "name";

	@JsonProperty
	@ApiModelProperty(value = "Name conditions by rus", required = true)
	private String conditionsRus;

	@JsonProperty
	@ApiModelProperty(value = "Name conditions by kz", required = true)
	private String conditionsKz;

	@JsonProperty
	@ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
	private Float coefficient;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Standard units", required = true)
	private UnitEnum units;
}