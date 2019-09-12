package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.WeaponTypeEnum;

@Accessors(chain = true)
@Data
public class WeaponIpscCode {
	
	@JsonProperty
	@ApiModelProperty(value = "IPSC code")
	private String code;

	@JsonProperty("name")
	@ApiModelProperty(value = "Type of a weapon")
	private WeaponTypeEnum typeWeapon;
}
