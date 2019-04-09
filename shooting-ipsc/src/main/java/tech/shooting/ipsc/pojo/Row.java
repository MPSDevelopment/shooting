package tech.shooting.ipsc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Row {
	private Ask ask;

	private Answer answer;
}
