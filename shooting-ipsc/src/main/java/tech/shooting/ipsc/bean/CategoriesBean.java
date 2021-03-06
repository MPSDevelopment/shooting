package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Category;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class CategoriesBean {

    @JsonProperty
    @ApiModelProperty(value = "Category id", required = true)
    private Category category;
    
    @JsonProperty
    @ApiModelProperty(value = "Excellent execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Float excellentTime;
    
    @JsonProperty
    @ApiModelProperty(value = "Good execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Float goodTime;
    
    @JsonProperty("satTime")
    @ApiModelProperty(value = "Standard execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Float satisfactoryTime;
}
