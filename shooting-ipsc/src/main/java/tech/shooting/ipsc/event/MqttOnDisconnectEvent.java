package tech.shooting.ipsc.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class MqttOnDisconnectEvent extends Event {

	private String clientId;
}
