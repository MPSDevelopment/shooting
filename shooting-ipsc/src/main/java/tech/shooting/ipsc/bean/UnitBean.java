package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class UnitBean {

    @JsonProperty
    @ApiModelProperty(value = "Units name")
    @NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
    private String name;
}
