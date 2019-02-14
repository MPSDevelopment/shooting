package com.mpsdevelopment.shooting.eventbus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	private String source;
	private String publisherId;


}
