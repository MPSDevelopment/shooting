package tech.shooting.ipsc.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class WorkSpaceBean {
    @JsonProperty
    @ApiModelProperty(value = "Worspace id  ", required = true)
    @NotNull
    private long id;

    @JsonProperty
    @ApiModelProperty(value = "Ip address", required = true)
    @NotEmpty
    private String ip;

    @JsonProperty
    @ApiModelProperty(value = "Test", required = true)
    @NotNull
    private long test;

    @JsonProperty
    @ApiModelProperty(value = "Person who pass the test", required = true)
    @NotNull
    private long person;

}
