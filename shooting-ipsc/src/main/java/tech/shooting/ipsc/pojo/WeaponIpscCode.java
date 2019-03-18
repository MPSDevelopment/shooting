package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import tech.shooting.ipsc.enums.WeaponTypeEnum;

public class WeaponIpscCode {

	@JsonProperty
	@ApiModelProperty(value = "IPSC code")
	private String code;

	@JsonProperty("weaponType")
	@ApiModelProperty(value = "Type of a weapon")
	private WeaponTypeEnum typeWeapon;
}
