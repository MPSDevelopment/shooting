package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.WeaponIpscCode;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Person create")
@EqualsAndHashCode(callSuper = false)
@ToString
public class PersonBean{
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
	private Rank rank;

	@JsonProperty
	@ApiModelProperty(value = "Person's division")
	private Division division;

	@JsonProperty("level")
	@ApiModelProperty(value = "Person's qualifier rank", required = true)
	@NotNull(message= ValidationConstants.LEVEL_MESSAGE)
	private ClassificationBreaks qualifierRank;

	@JsonProperty
	@ApiModelProperty(value = "Person's IPSC codes")
	private List<WeaponIpscCode> codes;
}
