package com.mpsdevelopment.shooting.eventbus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	private String source;
	private String publisherId;


}
