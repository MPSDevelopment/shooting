package tech.shooting.ipsc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SubjectsName {
	private String useName;

	private String name;
}
