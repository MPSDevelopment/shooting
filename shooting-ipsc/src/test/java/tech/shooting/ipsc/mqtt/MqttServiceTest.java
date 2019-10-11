package tech.shooting.ipsc.mqtt;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.spring.ApplicationContextWrapper;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.event.MqttSimpleEvent;
import tech.shooting.ipsc.mqtt.JsonMqttCallBack;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.service.WorkspaceService;

import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.moquette.broker.ClientDescriptor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collection;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
@ContextConfiguration(classes = { TokenUtils.class, IpscMqttSettings.class, MqttService.class, WorkspaceService.class, JsonMqttCallBack.class, ApplicationContextWrapper.class, IpscMongoConfig.class })
@EnableConfigurationProperties
@EnableAutoConfiguration
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
//@Tag(IpscConstants.UNIT_TEST_TAG)
//@ActiveProfiles("simple")
//@TestMethodOrder(OrderAnnotation.class)
class MqttServiceTest {

	private static final String MQTT_REMOTE_URL = "tcp://40.69.138.82:1883";

	private static final String TEST_LOGIN = "login";

	private static final String TEST_PASSWORD = "password";

	private static final String TEST_TOPIC = "ipsc";

	private int count = 0;

	@Autowired
	private MqttService mqttService;

	@Autowired
	private IpscMqttSettings settings;

	private String mqttServerUrl;

	@BeforeEach
	public void before() throws IOException {

		log.info("Starting broker");

		assertNotNull(settings.getAdminLogin());
		assertNotNull(settings.getAdminPassword());

		settings.setIpscTopicName(TEST_TOPIC);

		assertNotNull(settings.getAdminLogin());

		mqttService.startBroker("config/moquette-protected.conf");

		mqttServerUrl = mqttService.getServerUrl();

		log.info("Broker started %s", mqttServerUrl);

		mqttService.getSubscribers();
	}

	@AfterEach
	public void after() throws MqttException {
		mqttService.stopBroker();
	}

//	@Test
	public void testPublishSubscribe() throws MqttException, InterruptedException {

		log.info("Started test");

		EventBus.subscribe(this);

		assertNotNull(settings.getGuestLogin());

		String topicName1 = "command/topic1";
		String topicName2 = "command/topic2";

		// subscriber

		MqttClient subscriber1 = mqttService.createSubscriber(mqttServerUrl, settings.getGuestLogin(), settings.getGuestPassword(), topicName1);
		MqttClient subscriber2 = mqttService.createSubscriber(mqttServerUrl, settings.getGuestLogin(), settings.getGuestPassword(), topicName2);
		MqttClient subscriber3 = mqttService.createSubscriber(mqttServerUrl, settings.getGuestLogin(), settings.getGuestPassword(), topicName2);

		assertThrows(MqttSecurityException.class, () -> {
			mqttService.createSubscriber(mqttServerUrl, TEST_LOGIN, TEST_PASSWORD, topicName2);
		});

		MqttClient subscriberAll = mqttService.createSubscriber(mqttServerUrl, settings.getAdminLogin(), settings.getAdminPassword(), "command/#");

		// publisher

		MqttClient publisher = mqttService.createPublisher(mqttServerUrl, settings.getGuestLogin(), settings.getGuestPassword());

		assertThrows(MqttException.class, () -> {
			mqttService.createPublisher(mqttServerUrl, TEST_LOGIN, TEST_PASSWORD);
		});

		// publish a message

		MqttMessage message = mqttService.createMessage("Crazy message");

		publisher.getTopic(topicName1).publish(message);
		publisher.getTopic(topicName2).publish(message);

		Collection<ClientDescriptor> subscribers = mqttService.getSubscribers();
		subscribers.forEach(item -> {
			log.info("Item = %s", item);
		});

		assertEquals(5, subscribers.size());

		Thread.sleep(100);

		publisher.disconnect();
		subscriber1.disconnect();
		subscriber2.disconnect();
		subscriber3.disconnect();
		subscriberAll.disconnect();

		assertEquals(5, count);

		subscribers = mqttService.getSubscribers();
		assertEquals(0, subscribers.size());
	}

//	@Test
	public void checkRemoteConnection() throws MqttException, InterruptedException {

		MqttClient subscriber = mqttService.createSubscriber(MQTT_REMOTE_URL, settings.getGuestLogin(), settings.getGuestPassword(), new MqttCallback() {

			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				log.info("Message arrived for topic %s: %s", topic, message.getPayload());
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}

		}, MqttConstants.COMPETITION_TOPIC);

		Thread.sleep(2000);

		subscriber.disconnect();

	}

	@Handler
	public void handle(MqttSimpleEvent event) {
		count++;
	}

}
