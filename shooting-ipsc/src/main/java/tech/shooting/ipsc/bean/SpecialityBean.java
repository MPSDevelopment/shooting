package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class SpecialityBean {
    @JsonProperty
    @ApiModelProperty(value = "Speciality name in rus", required = true)
    @NotBlank(message = ValidationConstants.SPECIALITY_MESSAGE)
    @Size(min = 3, message = ValidationConstants.SPECIALITY_SIZE_MESSAGE)
    private String specialityRus;

    @JsonProperty
    @ApiModelProperty(value = "Speciality name in kz", required = true)
    @NotBlank(message = ValidationConstants.SPECIALITY_MESSAGE)
    @Size(min = 3, message = ValidationConstants.SPECIALITY_SIZE_MESSAGE)
    private String specialityKz;
}
