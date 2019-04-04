package tech.shooting.ipsc.mqtt;

import java.nio.charset.StandardCharsets;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublisherListener extends AbstractInterceptHandler {

	@Override
	public String getID() {
		return "EmbeddedLauncherPublishListener";
	}

	@Override
	public void onPublish(InterceptPublishMessage msg) {
		final String decodedPayload = new String(ByteBufUtil.getBytes(msg.getPayload()), StandardCharsets.UTF_8);
		log.info("Received on topic: " + msg.getTopicName() + " content: " + decodedPayload);
	}
}