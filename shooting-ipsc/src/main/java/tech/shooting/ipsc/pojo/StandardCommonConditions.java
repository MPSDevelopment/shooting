package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Positive;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.validator.ValidationConstants;

@Data
@Accessors(chain = true)
@Document(collection = "standardCommonConditions")
@TypeAlias("standardCommonConditions")
@ToString(callSuper = true)
public class StandardCommonConditions  extends BaseDocument {
	
    public static final String COMMON_CONDITION_UNITS = "name";
    @JsonProperty
    @ApiModelProperty(value = "Name conditions by rus", required = true)
    private String conditionsRus;

    @JsonProperty
    @ApiModelProperty(value = "Name conditions by kz", required = true)
    private String conditionsKz;

    @JsonProperty
    @ApiModelProperty(value = "Standard coefficient time for this conditions", required = true)
    @Positive(message = ValidationConstants.POSITIVE_MESSAGE)
    private Double coefficient;

    @JsonProperty
    @ApiModelProperty(value = "Standard units", required = true)
    private UnitEnum units;
}
