package tech.shooting.tag.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.shooting.commons.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class MqttOnConnectEvent extends Event {

	private String clientId;
	
	private String topic;

}
