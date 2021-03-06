package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class Category extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Name category by rus", required = true)
    @NotBlank
    private String nameCategoryRus;

    @JsonProperty
    @ApiModelProperty(value = "Name category by kz", required = true)
    @NotBlank
    private String nameCategoryKz;

}
