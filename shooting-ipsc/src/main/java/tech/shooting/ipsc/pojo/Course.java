package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import java.time.LocalDate;

@Document(collection = "course")
@TypeAlias("course")
@Data
@Accessors(chain = true)
public class Course extends BaseDocument {
    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Person", required = true)
    private Person person;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Division", required = true)
    private Division division;

    @JsonProperty
    @ApiModelProperty(value = "Course name", required = true)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Address location of courses")
    private String address;

    @JsonProperty
    @ApiModelProperty(value = "Date of course", required = true)
    private LocalDate date;

    @JsonProperty
    @ApiModelProperty(value = "Course image path", required = true)
    private String imagePath;
}
