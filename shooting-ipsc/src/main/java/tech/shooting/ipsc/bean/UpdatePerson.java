package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
public class UpdatePerson {

	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@ApiModelProperty(value = "Person's id")
	private long id;

	@JsonProperty
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

	@JsonProperty
	@ApiModelProperty(value = "Person's rank", required = true)
	private String rank;

	@JsonProperty
	@ApiModelProperty(value = "Person's rifle IPSC code")
	private String rifleCodeIpsc;

	@JsonProperty
	@ApiModelProperty(value = "Person's shotgun IPSC code")
	private String shotgunCodeIpsc;

	@JsonProperty
	@ApiModelProperty(value = "Person's handgun IPSC code")
	private String handgunCodeIpsc;

	@JsonProperty
	@ApiModelProperty(value = "Person's type weapon", required = true)
	private ExerciseWeaponTypeEnum typeWeapon;

	@JsonProperty
	@ApiModelProperty(value = "Person's qualifier rank")
	private String qualifierRank;
}
