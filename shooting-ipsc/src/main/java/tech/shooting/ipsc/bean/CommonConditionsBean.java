package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonConditionsBean {
    @JsonProperty
    @ApiModelProperty(value = "Name conditions by rus", required = true)
    private String conditionsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by kz", required = true)
    private String conditionsKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
    private Double coefficient;

    @JsonProperty
    @ApiModelProperty(value = "Standard units", required = true)
    private Long units;

    @JsonProperty
    @ApiModelProperty(value = "Condition min value")
    private Double minValue;

    @JsonProperty
    @ApiModelProperty(value = "Condition max value")
    private Double maxValue;
}
