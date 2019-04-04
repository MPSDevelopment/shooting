package tech.shooting.ipsc.bean;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Data
@Accessors(chain = true)
public class AggBean {
	List<String> stat;

	private Person person;

	private Integer time;
}
