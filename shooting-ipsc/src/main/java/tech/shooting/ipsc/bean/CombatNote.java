package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
public class CombatNote {
	@JsonProperty
	@ApiModelProperty(value = "Filling date")
	@NotNull
	private OffsetDateTime date;

	@JsonProperty
	@ApiModelProperty(value = "Responsibility person id")
	private Person combat;

	@JsonProperty
	@ApiModelProperty
	private Division division;
}
