package tech.shooting.speedway.pojo;

import lombok.Data;

@Data
public class Tag {

	private String code;

	private long firstSeenTime;

	private long lastSeenTime;

}
