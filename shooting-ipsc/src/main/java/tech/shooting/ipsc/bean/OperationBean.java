package tech.shooting.ipsc.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Info;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Weather;

@Data
@Accessors(chain = true)
public class OperationBean {
	
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
    private List<OperationParticipant> participants;
    
    @JsonProperty
    @ApiModelProperty(value = "Operation symbols list", required = true)
    private List<OperationSymbol> symbols;
}
