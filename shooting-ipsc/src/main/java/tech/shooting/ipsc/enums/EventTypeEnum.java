package tech.shooting.ipsc.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EventTypeEnum {
	COMPETITION_UPDATED, COMPETITION_STOPPED, MQTT_CONNECT, MQTT_CONNECTION_LOST, MQTT_DISCONNECT, WORKSPACE_CHANGED, TEST_STARTED, TEST_FINISHED ,TAG_DETECTED, TAG_IMITATOR_ONLY_CODES_STARTED, TAG_IMITATOR_STARTED, TAG_UNDETECTED, TAG_IMITATOR_FINISHED, RUNNING_COMPLETED,  RUNNING_UPDATED, RUNNING_CONNECT, RUNNING_DISCONNECT

	
}
