package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Info;

@Data
@Accessors(chain = true)
public class OperationBean {
	
	@JsonProperty
	@ApiModelProperty(value = "Operation info", required = true)
	private Info info;
	
	@JsonProperty
	@ApiModelProperty(value = "Operation image path")
	private String imagePath;
}
