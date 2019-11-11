package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class CourseBean {
	
    @JsonProperty
    @ApiModelProperty(value = "Person id", required = true)
    @NotNull
    private Long owner;

    @JsonProperty
    @ApiModelProperty(value = "Course name", required = true)
    @Size(min = 3, message = ValidationConstants.COURSE_NAME_MESSAGE)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Address location of courses")
    @NotBlank
    private String address;

    @JsonProperty
    @ApiModelProperty(value = "Date of course", required = true)
    @NotNull
    private OffsetDateTime date;

    @JsonProperty
    @ApiModelProperty(value = "Course image path", required = true)
    @NotBlank
    private String imagePath;
}
