package tech.shooting.ipsc;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.mqtt.JsonMqttCallBack;
import tech.shooting.ipsc.mqtt.event.MqttSimpleEvent;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBufUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JsonMqttCallBack.class })
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ActiveProfiles("simple")
class MqttTest {

	private static final String MQTT_URL = "tcp://127.0.0.1:1883";

	private static final String TEST_LOGIN = "login";

	private static final String TEST_PASSWORD = "password";

	private int count = 0;

	private static Server mqttBroker;

	@BeforeAll
	public static void beforeAll() throws IOException {
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

		mqttBroker = new Server();
		List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener());
		mqttBroker.startServer(classPathConfig, userHandlers);

		log.info("Broker started press [CTRL+C] to stop");
	}
	
	@AfterAll
	public static void afterAll() {
		mqttBroker.stopServer();
	}

	@Test
	public void testPublishSubscribe() throws MqttException, InterruptedException {

		EventBus.subscribe(this);

		String topicName1 = "command/topic1";

		String topicName2 = "command/topic2";

		// subscriber

		var subscriber1 = createSubscriber(MQTT_URL, topicName1);
		var subscriber2 = createSubscriber(MQTT_URL, topicName2);
		var subscriber3 = createSubscriber(MQTT_URL, topicName2);

		var subscriberAll = createSubscriber(MQTT_URL, "command/#");

		// publisher

		var publisher = createPublisher(MQTT_URL);

		// publish a message

		MqttMessage message = createMessage("Crazy message");

		MqttTopic topic1 = publisher.getTopic(topicName1);
		topic1.publish(message);

		MqttTopic topic2 = publisher.getTopic(topicName2);
		topic2.publish(message);

		Thread.sleep(100);

		publisher.disconnect();
		subscriber1.disconnect();
		subscriber2.disconnect();
		subscriber3.disconnect();
		subscriberAll.disconnect();

		assertEquals(5, count);

	}

	private MqttMessage createMessage(String payload) {
		MqttMessage message = new MqttMessage();
		message.setQos(1);
		message.setRetained(false);
		message.setPayload(payload.getBytes());
		return message;
	}

	private MqttClient createPublisher(String url) throws MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(TEST_LOGIN);
		connOpts.setPassword(TEST_PASSWORD.toCharArray());

		String publisherId = UUID.randomUUID().toString();
		var publisher = new MqttClient(url, publisherId);
		publisher.connect(connOpts);
		return publisher;
	}

	private MqttClient createSubscriber(String url, String... topicNames) throws MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(TEST_LOGIN);
		connOpts.setPassword(TEST_PASSWORD.toCharArray());

		var subscriber = new MqttClient(url, MqttClient.generateClientId());
		subscriber.setCallback(new JsonMqttCallBack());
		subscriber.connect(connOpts);
		Arrays.asList(topicNames).forEach(topicName -> {
			try {
				subscriber.subscribe(topicName);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		});

		return subscriber;
	}

	@Handler
	public void handle(MqttSimpleEvent event) {
		count++;
	}

	static class PublisherListener extends AbstractInterceptHandler {

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

}
