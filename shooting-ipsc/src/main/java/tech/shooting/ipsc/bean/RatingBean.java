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
	@ApiModelProperty(value = "Person id", required = true)
	private Long personId;
	
	@JsonProperty
	@ApiModelProperty(value = "Person score", required = true)
	private Long score;
	
	@JsonProperty
	@ApiModelProperty(value = "Person time of execution", required = true)
	private Long timeOfExercise;
	
	@JsonProperty
	private List<Score> scores = new ArrayList<>();

}
