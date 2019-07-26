package tech.shooting.ipsc.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class WorkSpaceBean {
	
	@JsonProperty
	@ApiModelProperty(value = "Mqtt Client id", required = true)
	private String clientId;
	
	@JsonProperty
	@ApiModelProperty(value = "Workspace name", required = true)
	private String name;

    @JsonProperty
    @ApiModelProperty(value = "Quiz id", required = true)
    @NotNull
    private long quizId;

    @JsonProperty
    @ApiModelProperty(value = "Person id", required = true)
    @NotNull
    private long personId;

}
