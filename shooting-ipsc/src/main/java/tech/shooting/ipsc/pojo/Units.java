package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Accessors(chain = true)
@Document(collection = "units")
@TypeAlias("units")
@ToString(callSuper = true)
public class Units extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Units name")
    private String units;
}
