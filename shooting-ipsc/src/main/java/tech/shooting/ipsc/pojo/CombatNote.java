package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.bean.Stat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@Document(collection = "combatenote")
@TypeAlias("combatenote")
public class CombatNote extends BaseDocument {
	@JsonProperty
	@ApiModelProperty(value = "Filling date")
	@NotNull
	private LocalDate date;

	@DBRef
	@JsonProperty
	@ApiModelProperty (value = "Responsibility user id")
	private User combat;

	@DBRef
	@JsonProperty
	@ApiModelProperty
	private Division division;

	@JsonProperty
	@ApiModelProperty(value = "List status")
	@NotNull
	private List<Stat> statList;
}
