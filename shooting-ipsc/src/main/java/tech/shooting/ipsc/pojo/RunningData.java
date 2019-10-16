package tech.shooting.ipsc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RunningData {

	private Person person;
	
	private long firstTime;
	
	private long lastTime;
	
	private int laps = 0;
}
