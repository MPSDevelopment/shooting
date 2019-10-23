package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChangeRfidCodeBean {

	private static final String USER_ID = "id";

	@JsonProperty(USER_ID)
	@NotNull(message = ValidationConstants.USER_ID_MESSAGE)
	private Long id;

	@JsonProperty
	@ApiModelProperty(value = "Person's rfid code")
	@NotNull(message = ValidationConstants.RFID_CODE_MEASSAGE)
	private String rfidCode;
}
