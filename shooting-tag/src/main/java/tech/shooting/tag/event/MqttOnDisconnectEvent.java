package tech.shooting.tag.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.tag.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class MqttOnDisconnectEvent extends Event {

	private String clientId;
}
