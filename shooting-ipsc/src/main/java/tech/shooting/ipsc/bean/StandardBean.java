package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.StandardFails;
import tech.shooting.ipsc.pojo.CategoryByPoints;
import tech.shooting.ipsc.pojo.CategoryByTime;
import tech.shooting.ipsc.pojo.Info;

import java.util.List;

import javax.validation.constraints.PositiveOrZero;

@Data
@Accessors(chain = true)
public class StandardBean {

    @JsonProperty
    @ApiModelProperty(value = "Info from standard", required = true)
    private Info info;

    @JsonProperty
    @ApiModelProperty(value = "Subject id", required = true)
    private Long subject;

    @JsonProperty
    @ApiModelProperty(value = "Is active", required = true)
    private boolean active;

    @JsonProperty
    @ApiModelProperty(value = "Is groups", required = true)
    private boolean groups;
    
    @JsonProperty
    @ApiModelProperty(value = "Is running", required = true)
    private boolean running;
    
	@JsonProperty
	@ApiModelProperty(value = "Number of laps to finish the standard", required = true)
	@PositiveOrZero
	private Integer laps;

	@JsonProperty
	@ApiModelProperty(value = "List categories by time")
	private List<CategoryByTime> categoryByTimeList;
	
	@JsonProperty
	@ApiModelProperty(value = "List categories by points")
	private List<CategoryByPoints> categoryByPointsList;

    @JsonProperty
    @ApiModelProperty(value = "List fails")
    private List<StandardFails> failsList;

    @JsonProperty
    @ApiModelProperty(value = "List conditions")
    private List<ConditionsBean> conditionsList;
}
