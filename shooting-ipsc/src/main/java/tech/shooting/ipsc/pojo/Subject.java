package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

import javax.validation.constraints.NotBlank;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@Document(collection = "subject")
@TypeAlias("subject")
public class Subject extends BaseDocument {
	public static final String RUS = "rus";

	public static final String KZ = "kz";

	@JsonProperty
	@ApiModelProperty(value = "Subject in rus", required = true)
	@NotBlank
	private String rus;

	@JsonProperty
	@ApiModelProperty(value = "Subject in kz", required = true)
	@NotBlank
	private String kz;
}
