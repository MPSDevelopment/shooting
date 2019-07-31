package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.serialization.BaseDocumentIdSerializer;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "division")
@TypeAlias("division")
@Data
@ToString(exclude = "children")
@Accessors(chain = true)
public class Division extends BaseDocument {

	public static final String NAME_WITH_PARENT = "name and parent id";

	public static final String PARENT_FIELD = "parent";

	public static final String CHILDREN_FIELD = "children";

	@JsonProperty
	@ApiModelProperty(value = "Parent name", required = true, hidden = true)
	@DBRef
	@JsonSerialize(using = BaseDocumentIdSerializer.class)
	private Division parent;

	@JsonProperty
	@ApiModelProperty(value = "Division name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "List of children", hidden = true)
	@DBRef
	private List<Division> children = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Status division")
	private boolean active;

	@JsonIgnore
	public List<Division> getAllChildren() {
		var list = new ArrayList<Division>();
		list.add(this);
		for (var child : children) {
			list.add(child);
			list.addAll(child.getAllChildren());
		}
		return list;
	}
}
