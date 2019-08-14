package tech.shooting.ipsc.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class WorkspaceBean {
	
	@JsonProperty
	@ApiModelProperty(value = "Mqtt Client id", required = true)
	private String clientId;
	
	@JsonProperty
	@ApiModelProperty(value = "Workspace name", required = true)
	private String name;
	
	@JsonProperty
	@ApiModelProperty(value = "Subject id")
	@DBRef
	private Long subjectId;

    @JsonProperty
    @ApiModelProperty(value = "Quiz id", required = true)
    @NotNull
    private Long quizId;

    @JsonProperty
    @ApiModelProperty(value = "Person id", required = true)
    @NotNull
    private Long personId;
    
    @JsonProperty
    @ApiModelProperty(value = "Use workspace in test", required = true)
    private boolean useInTest = false;
    
    @JsonProperty
    @ApiModelProperty(value = "Just check workspace", required = true)
    private boolean check = false;

}
