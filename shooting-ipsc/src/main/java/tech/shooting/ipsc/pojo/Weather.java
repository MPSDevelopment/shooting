package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Weather {

    @JsonProperty
    @ApiModelProperty(value = "Weather temperature Celsius degrees", required = true)
    private Float temperature;
    
    @JsonProperty
    @ApiModelProperty(value = "Weather humidity %", required = true)
    private Float humidity;
    
    @JsonProperty
    @ApiModelProperty(value = "Weather wind speed m/s", required = true)
    private Float windSpeed;
    
    @JsonProperty
    @ApiModelProperty(value = "Weather wind direction degrees", required = true)
    private Float windDirection;
}
