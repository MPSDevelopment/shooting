package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.WorkspaceStatusEnum;

@Document(collection = "workspace")
@TypeAlias("workspace")
@Data
@Accessors(chain = true)
public class Workspace extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Status", required = true)
	private WorkspaceStatusEnum status;
	
	@JsonProperty
	@ApiModelProperty(value = "Mqtt Client id", required = true)
	private String clientId;
	
	@JsonProperty
	@ApiModelProperty(value = "Workspace name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Ip address", required = true)
	private String ip;

	@JsonProperty
	@ApiModelProperty(value = "Quiz id")
	@DBRef
	private Long quizId;

	@JsonProperty
	@ApiModelProperty(value = "Person id")
	@DBRef
	private Long personId;

    @JsonProperty
    @ApiModelProperty(value = "Use workspace in test", required = true)
    private boolean useInTest;
	
}
