package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "workspace")
@TypeAlias("workspace")
@Data
@Accessors(chain = true)
public class WorkSpace extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Ip address", required = true)
    private String ip;

    @JsonProperty
    @ApiModelProperty(value = "Test")
    @DBRef
    private Quiz test;

    @JsonProperty
    @ApiModelProperty(value = "Person who pass the test")
    @DBRef
    private Person person;

}
