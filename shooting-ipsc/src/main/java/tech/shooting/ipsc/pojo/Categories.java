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
public class Categories {

    @JsonProperty
    @ApiModelProperty(value = "Name category by rus", required = true)
    private String nameCategoryRus;

    @JsonProperty
    @ApiModelProperty(value = "Name category by kz", required = true)
    private String nameCategoryKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Long time;
}
