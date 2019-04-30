package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
@Document(collection = "standard")
@TypeAlias("standard")
@ToString(callSuper = true)
public class CommonConditions  extends BaseDocument {
    public static final String COMMON_CONDITION_UNITS = "units";
    @JsonProperty
    @ApiModelProperty(value = "Name conditions by rus", required = true)
    private String conditionsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by kz", required = true)
    private String conditionsKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
    private Double coefficient;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Standard units", required = true)
    private Units units;

    @JsonProperty
    @ApiModelProperty(value = "Condition min value")
    private Double minValue;

    @JsonProperty
    @ApiModelProperty(value = "Condition max value")
    private Double maxValue;
}
