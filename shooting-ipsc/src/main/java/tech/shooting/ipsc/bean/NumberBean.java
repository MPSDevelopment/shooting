package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Rfid")
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@ToString
public class NumberBean {

	@JsonProperty
	@ApiModelProperty(value = "Number")
	private String number;
}
