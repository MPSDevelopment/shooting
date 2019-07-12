package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.converter.OffsetDateDeserializer;
import tech.shooting.commons.mongo.converter.OffsetDateSerializer;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class CombatNoteBean {
	@JsonProperty
	@ApiModelProperty(value = "Filling date")
	@NotNull(message = ValidationConstants.DATE_MESSAGE)
    @JsonSerialize(using = OffsetDateSerializer.class)
    @JsonDeserialize(using = OffsetDateDeserializer.class)
	private OffsetDateTime date;

	@JsonProperty
	@ApiModelProperty(value = "Responsibility person id")
	@NotNull(message = ValidationConstants.PERSON_ID)
	private Long combatId;
}
