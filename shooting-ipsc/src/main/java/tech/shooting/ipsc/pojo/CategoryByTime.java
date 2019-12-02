package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class CategoryByTime {

//    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Standard category", required = true)
    private Category category;
    
    @JsonProperty
    @ApiModelProperty(value = "Excellent execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Integer excellentTime;
    
    @JsonProperty
    @ApiModelProperty(value = "Good execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Integer goodTime;
    
    @JsonProperty("satTime")
    @ApiModelProperty(value = "Standard execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Integer satisfactoryTime;
}
