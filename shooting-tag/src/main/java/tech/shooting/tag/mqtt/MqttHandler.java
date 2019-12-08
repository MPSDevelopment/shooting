package tech.shooting.tag.mqtt;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.event.MqttOnConnectEvent;
import tech.shooting.tag.event.MqttOnConnectionLostEvent;
import tech.shooting.tag.event.MqttOnDisconnectEvent;
import tech.shooting.tag.eventbus.EventBus;

@Slf4j
public class MqttHandler implements InterceptHandler {

	@Override
	public String getID() {
		return "Main handler";
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnect(InterceptConnectMessage arg0) {
		log.info("Mqtt connect ClientId %s Will topic %s Username %s", arg0.getClientID(), arg0.getWillTopic(), arg0.getUsername());
		EventBus.publishEvent(new MqttOnConnectEvent(arg0.getClientID(), arg0.getWillTopic()));
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage arg0) {
		log.info("Mqtt connection loss ClientId %s Username %s", arg0.getClientID(), arg0.getUsername());
		EventBus.publishEvent(new MqttOnConnectionLostEvent(arg0.getClientID()));
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage arg0) {
		log.info("Mqtt disconnection loss ClientId %s Username %s", arg0.getClientID(), arg0.getUsername());
		EventBus.publishEvent(new MqttOnDisconnectEvent(arg0.getClientID()));
	}

	@Override
	public void onMessageAcknowledged(InterceptAcknowledgedMessage arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPublish(InterceptPublishMessage arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage arg0) {
		log.info("Mqtt subscription ClientId %s Topic filter %s Username %s", arg0.getClientID(), arg0.getTopicFilter(), arg0.getUsername());
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage arg0) {
		// TODO Auto-generated method stub

	}

}
