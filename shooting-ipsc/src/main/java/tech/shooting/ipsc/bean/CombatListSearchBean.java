package tech.shooting.ipsc.bean;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.converter.OffsetDateDeserializer;
import tech.shooting.commons.mongo.converter.OffsetDateSerializer;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;

@Getter
@Setter
@Accessors(chain = true)
public class CombatListSearchBean {

	@JsonProperty
	@ApiModelProperty(value = "Division id")
	private Long divisionId;

	@JsonProperty
	@ApiModelProperty(value = "Status is present")
	private TypeOfPresence status;

	@JsonProperty
	@ApiModelProperty(value = "Interval")
	private TypeOfInterval interval;

	@JsonProperty
    @JsonSerialize(using = OffsetDateSerializer.class)
    @JsonDeserialize(using = OffsetDateDeserializer.class)
	private OffsetDateTime date;
}
