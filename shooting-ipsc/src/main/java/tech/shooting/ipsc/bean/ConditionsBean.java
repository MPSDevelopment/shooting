package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.enums.UnitEnum;

import org.springframework.data.mongodb.core.mapping.DBRef;
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class ConditionsBean {
	
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
    @ApiModelProperty(value = "Units id", required = true)
    private UnitEnum units;
}
