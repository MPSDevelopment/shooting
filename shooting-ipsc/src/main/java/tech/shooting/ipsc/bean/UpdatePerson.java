package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.WeaponIpscCode;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class UpdatePerson {

	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(value = "Person's id")
	private long id;

	@JsonProperty("userName")
	@ApiModelProperty(value = "Person's name", required = true)
	@NotNull(message = ValidationConstants.PERSON_NAME_MESSAGE)
	@Size(min = 3, max = 20, message = ValidationConstants.PERSON_NAME_MESSAGE)
	private String name;
	@JsonProperty
	@ApiModelProperty(value = "Person's birthday")
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "Person's address")
	private Address address;

	@JsonProperty
	@ApiModelProperty(value = "Person's team")
	private String team;

	@JsonProperty("weaponType")
	@ApiModelProperty(value = "Person's type weapon", required = true)
	private WeaponTypeEnum typeWeapon;

	@JsonProperty
	@ApiModelProperty(value = "Person's active ")
	private boolean active;

	@JsonProperty
	@ApiModelProperty(value = "Person's rank")
	private String rank;

	@JsonProperty("level")
	@ApiModelProperty(value = "Person's qualifier rank", required = true)
	private ClassificationBreaks qualifierRank;

	@JsonProperty
	@ApiModelProperty(value = "Person's IPSC codes")
	private List<WeaponIpscCode> codes;
}
