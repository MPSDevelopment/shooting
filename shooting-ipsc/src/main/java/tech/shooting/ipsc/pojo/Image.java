package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.experimental.Accessors;
import tech.shooting.commons.mongo.BaseDocument;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Document(collection = "image")
@TypeAlias("image")
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BaseDocument {

	@JsonProperty
	private String fileName;

	@JsonProperty
	private String type;
	
	
}
