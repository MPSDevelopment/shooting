package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.StandardPassEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Document(collection = "standardScore")
@TypeAlias("standardScore")
@Data
@Accessors(chain = true)
public class StandardScore extends BaseDocument {
	
	public static final String STANDARD_FIELD = "standardId";
	
	public static final String PERSON_FIELD = "personId";
	
	public static final String TIME_FIELD = "datetime";
	
	@JsonProperty
	@ApiModelProperty(value = "Standard id", required = true)
	@NotNull(message = ValidationConstants.STANDARD_ID)
	private Long standardId;

	@JsonProperty
	@ApiModelProperty(value = "Person id", required = true)
	@NotNull(message = ValidationConstants.PERSON_ID)
	private Person person;
	
	@JsonProperty
	@ApiModelProperty(value = "Score's datetime")
	private OffsetDateTime datetime = OffsetDateTime.now();

	@JsonProperty
	@ApiModelProperty(value = "Person score of this standard", required = true)
	@NotNull(message = ValidationConstants.SCORE_MESSAGE)
	private Integer score = 0;
	
	@JsonProperty
	@ApiModelProperty(value = "Person pass score of this standard", required = true)
	private StandardPassEnum passScore = StandardPassEnum.SATISFACTORY;

	@JsonProperty
	@ApiModelProperty(value = "Person time of executing this standard", required = true)
	@PositiveOrZero(message = ValidationConstants.TIME_MESSAGE)
	private double timeOfExercise = 0;
	
	@JsonProperty
	@ApiModelProperty(value = "Person points this standard", required = true)
	@PositiveOrZero(message = ValidationConstants.POINTS_MESSAGE)
	private double points = 0;

	@JsonProperty
	@ApiModelProperty(value = "Disqualification description")
	private String disqualificationReason;
}
