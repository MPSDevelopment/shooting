package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Only id bean")
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@ToString
public class IdBean {

	@JsonProperty
	private Long id;
}
