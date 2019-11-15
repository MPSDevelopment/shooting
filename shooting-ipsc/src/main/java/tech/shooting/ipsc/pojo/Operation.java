package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "operation")
@TypeAlias("operation")
@Data
@Accessors(chain = true)
public class Operation extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Operation info", required = true)
	private Info info;

	@JsonProperty
	@ApiModelProperty(value = "Operation weather", required = true)
	private Weather weather;

	@JsonProperty  
	@ApiModelProperty(value = "Operation image path", required = true)
	private String imagePath;

	@JsonProperty
	@ApiModelProperty(value = "Operation participant list", required = true)
	private List<OperationParticipant> participants = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Operation symbols list", required = true)
	private List<OperationSymbol> symbols = new ArrayList<>();

}
