package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import java.util.List;

@Document(collection = "division")
@TypeAlias("division")
@Data
@Accessors(chain = true)
public class Division extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "Parent name", required = true)
	private String root;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "List of child", required = true)
	private List<Division> child;

	@JsonProperty
	@ApiModelProperty(value = "Status division", required = true)
	private boolean active;
}
