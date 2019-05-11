package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class Conditions {
public static final String UNIT = "name";

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by rus", required = true)
    private String conditionsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by kz", required = true)
    private String conditionsKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
    private Long coefficient;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Standard name", required = true)
    private Units units;
}
