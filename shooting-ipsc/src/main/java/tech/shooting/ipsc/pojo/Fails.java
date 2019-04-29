package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class Fails {

    @JsonProperty
    @ApiModelProperty(value = "Name fail by rus", required = true)
    private String nameFailsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name fail by kz", required = true)
    private String nameFailsKz;


    @JsonProperty
    @ApiModelProperty(value = "Minus point for this fail", required = true)
    private Long minusPoint;
}
