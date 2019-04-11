package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "validation data")
@EqualsAndHashCode(callSuper = false)
@ToString
public class ValidationBean {

	@JsonProperty
	private String pattern;

	@JsonProperty
	private Integer minLength;

	@JsonProperty
	private Integer maxLength;

	@JsonProperty
	private Long min;

	@JsonProperty
	private Long max;

	@JsonProperty
	private Boolean required;

	@JsonProperty
	private Boolean requiredTrue;

	@JsonProperty
	private Boolean requiredFalse;

}
