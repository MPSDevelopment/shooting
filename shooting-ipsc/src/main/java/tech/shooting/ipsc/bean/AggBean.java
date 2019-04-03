package tech.shooting.ipsc.bean;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Person;

@Data
@Accessors(chain = true)
public class AggBean {
	private Integer time;

	private Person person;
}
