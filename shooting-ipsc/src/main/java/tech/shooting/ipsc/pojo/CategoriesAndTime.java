package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class CategoriesAndTime {
    @JsonProperty
    @ApiModelProperty(value = "Standard execute time for this category", required = true)
    @Positive(message = ValidationConstants.TIME_MESSAGE)
    private Long time;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Standard category", required = true)
    private Categories category;
}