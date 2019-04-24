package tech.shooting.ipsc.pojo;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Data
@Document(collection = "rank")
@TypeAlias("rank")
@Accessors(chain = true)
public class Rank extends BaseDocument {
	
	public static final String RUS = "rus";

	public static final String KZ = "kz";

    @JsonProperty
    @ApiModelProperty(value = "Rank name in rus", required = true)
    private String rus;

    @JsonProperty
    @ApiModelProperty(value = "Rank name in kz", required = true)
    private String kz;
    
    @JsonProperty
    @ApiModelProperty(value = "Officer or not")
    private boolean officer = false;
    
}
