package tech.shooting.tag.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.shooting.commons.eventbus.Event;

@AllArgsConstructor
@Getter
@Setter
public class MqttSimpleEvent extends Event {

    private String topicName;

    private String json;

}
