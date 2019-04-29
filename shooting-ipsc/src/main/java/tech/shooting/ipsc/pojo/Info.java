package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class Info {
    @JsonProperty
    @ApiModelProperty(value = "Named info by rus", required = true)
    private String namedRus;

    @JsonProperty
    @ApiModelProperty(value = "Named info by kz", required = true)
    private String namedKz;

    @JsonProperty
    @ApiModelProperty(value = "Description info by rus", required = true)
    private String descriptionRus;

    @JsonProperty
    @ApiModelProperty(value = "Description info by kz", required = true)
    private String descriptionKz;
}
