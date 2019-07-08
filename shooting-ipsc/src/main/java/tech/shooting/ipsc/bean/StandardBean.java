package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import tech.shooting.ipsc.pojo.Fails;
import tech.shooting.ipsc.pojo.Info;

import java.util.List;

@Data
@Accessors(chain = true)
public class StandardBean {

    @JsonProperty
    @ApiModelProperty(value = "Info from standard", required = true)
    private Info info;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Subject id", required = true)
    private Long subject;

    @JsonProperty
    @ApiModelProperty(value = "Is active", required = true)
    private boolean active;

    @JsonProperty
    @ApiModelProperty(value = "Is groups", required = true)
    private boolean groups;

    @JsonProperty
    @ApiModelProperty(value = "List categories", required = true)
    private List<CategoriesBean> categoriesList;

    @JsonProperty
    @ApiModelProperty(value = "List fails")
    private List<Fails> failsList;

    @JsonProperty
    @ApiModelProperty(value = "List conditions")
    private List<ConditionsBean> conditionsList;
}