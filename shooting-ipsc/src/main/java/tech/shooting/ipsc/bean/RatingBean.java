package tech.shooting.ipsc.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import tech.shooting.ipsc.pojo.Score;

@Data
public class RatingBean {

	@JsonProperty
	@ApiModelProperty(value = "Person id")
	private Long personId;

	@JsonProperty
	@ApiModelProperty(value = "Competitor score")
	private Long score;

	@JsonProperty
	@ApiModelProperty(value = "Competitor stages")
	private int stages = 0;

	@JsonProperty
	@ApiModelProperty(value = "Competitor time of execution")
	private double timeOfExercise = 0;

	@JsonProperty
	@ApiModelProperty(value = "Competitor hit factor")
	private double hitFactor = 0;

	@JsonProperty
	@ApiModelProperty(value = "Competitor percentage")
	private double percentage;

	@JsonProperty
	private List<Score> scores = new ArrayList<>();

}
