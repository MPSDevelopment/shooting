package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Document(collection = "speciality")
@TypeAlias("speciality")
@Accessors(chain = true)
public class Speciality extends BaseDocument {
    @JsonProperty
    @ApiModelProperty(value = "Speciality name in rus", required = true)
    @NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
    @Size(min = 3, message = ValidationConstants.SPECIALITY_SIZE_MESSAGE)
    private String specialityRus;

    @JsonProperty
    @ApiModelProperty(value = "Speciality name in kz", required = true)
    @NotBlank(message = ValidationConstants.NAME_NOT_BLANK_MESSAGE)
    @Size(min = 3, message = ValidationConstants.SPECIALITY_SIZE_MESSAGE)
    private String specialityKz;
}
