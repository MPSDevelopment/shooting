package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;

@Data
@Accessors(chain = true)
@ApiModel(value = "validation data")
@EqualsAndHashCode(callSuper = false)
@ToString
public class ValidationBean implements ValiationExportable {
	@JsonProperty
	private String fieldName;

	@JsonProperty
	private String name;

	@JsonProperty
	private String message;

	@JsonProperty
	private String pattern;

	@JsonProperty
	private Integer min;

	@JsonProperty
	private Integer max;

	@JsonProperty
	private Integer size;

	@JsonProperty
	private Long value;
}
