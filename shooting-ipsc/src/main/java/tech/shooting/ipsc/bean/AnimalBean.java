package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

@Data
@Accessors(chain = true)
public class AnimalBean {
	
	@Id
	@JsonProperty
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected Long id;

    @JsonProperty
    @ApiModelProperty(value = "Animal name", required = true)
    @Size(min = 3, max = 30, message = ValidationConstants.ANIMAL_NAME_MESSAGE)
    private String name;

    @JsonProperty("equipmentType")
    @ApiModelProperty(value = "Type of animal", required = true)
    private Long type;

    @JsonProperty
    @ApiModelProperty(value = "Animal owner")
    private Long owner;
}
