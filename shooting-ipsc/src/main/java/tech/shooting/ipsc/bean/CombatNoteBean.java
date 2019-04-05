package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class CombatNoteBean {
	@JsonProperty
	@ApiModelProperty(value = "Filling date")
	@NotNull
	private OffsetDateTime date;

	@JsonProperty
	@ApiModelProperty(value = "Responsibility person id")
	@NotNull
	private Long combatId;
}
