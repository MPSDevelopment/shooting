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
@ToString(callSuper = true)
@Document(collection = "category")
@TypeAlias("category")
public class Categories extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Name category by rus", required = true)
    private String nameCategoryRus;

    @JsonProperty
    @ApiModelProperty(value = "Name category by kz", required = true)
    private String nameCategoryKz;

}
