package tech.shooting.ipsc.pojo;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "settings")
@TypeAlias("settings")
@Data
@Accessors(chain = true)
public class Settings extends BaseDocument {
	
	public static final String NAME_FIELD = "name";
	
	@Indexed(unique = true)
	@JsonProperty
	private String name;

	@JsonProperty(value = "runIp")
	private String tagServiceIp;
}
