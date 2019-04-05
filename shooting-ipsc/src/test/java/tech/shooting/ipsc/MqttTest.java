package tech.shooting.ipsc;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.mqtt.JsonMqttCallBack;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.mqtt.PublisherListener;
import tech.shooting.ipsc.mqtt.event.MqttSimpleEvent;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.moquette.interception.InterceptHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MqttService.class, JsonMqttCallBack.class })
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
	
	@Autowired
	private MqttService mqttService;

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

		var subscriber1 = mqttService.createSubscriber(MQTT_URL, TEST_LOGIN, TEST_PASSWORD, topicName1);
		var subscriber2 = mqttService.createSubscriber(MQTT_URL, TEST_LOGIN, TEST_PASSWORD, topicName2);
		var subscriber3 = mqttService.createSubscriber(MQTT_URL, TEST_LOGIN, TEST_PASSWORD, topicName2);

		var subscriberAll = mqttService.createSubscriber(MQTT_URL, TEST_LOGIN, TEST_PASSWORD, "command/#");

		// publisher

		var publisher = mqttService.createPublisher(MQTT_URL, TEST_LOGIN, TEST_PASSWORD);

		// publish a message

		MqttMessage message = mqttService.createMessage("Crazy message");

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

	@Handler
	public void handle(MqttSimpleEvent event) {
		count++;
	}

}
