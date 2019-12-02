package tech.shooting.ipsc.service;

import lombok.Data;

@Data
public class TestRunningData {
	
	private int count;

	private int previousLaps = -1;

	private long previousTime;

	private long previousFirstTime;

}
