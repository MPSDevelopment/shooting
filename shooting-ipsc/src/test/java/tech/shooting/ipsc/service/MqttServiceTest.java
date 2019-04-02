package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.rabbitmq.event.MqttSimpleEvent;
import tech.shooting.ipsc.rabbitmq.mqtt.MqttRabbitService;
import tech.shooting.ipsc.rabbitmq.mqtt.SimpleMqttCallBack;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.settings.RabbitmqSettings;

import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { RestTemplate.class, TokenUtils.class, MqttRabbitService.class, SimpleMqttCallBack.class })
@SpringBootTest(classes = { RabbitmqSettings.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
class MqttServiceTest {

	private static final String TEST_LOGIN = "ip34fdsfdasfdsbgsc";

	private static final String TEST_PASSWORD = "iprewgbqw43283yausdhasvfassc";

	@Autowired
	private MqttRabbitService mqttRabbitService;

	private int count = 0;

	@Test
	public void testPublishSubscribe() throws MqttException, InterruptedException {

		EventBus.subscribe(this);

		String topicName1 = "command/topic1";

		String topicName2 = "command/topic2";

		// subscriber

		var subscriber1 = createSubscriber("tcp://127.0.0.1:1884", topicName1);
		var subscriber2 = createSubscriber("tcp://127.0.0.1:1884", topicName2);
		var subscriber3 = createSubscriber("tcp://127.0.0.1:1884", topicName2);
		
		var subscriberAll = createSubscriber("tcp://127.0.0.1:1884", "command/*");

		// publisher

		var publisher = createPublisher("tcp://localhost:1884");

		// publish a message

		MqttMessage message = createMessage("Crazy message");

		MqttTopic topic1 = publisher.getTopic(topicName1);
		topic1.publish(message);

		MqttTopic topic2 = publisher.getTopic(topicName2);
		topic2.publish(message);

		Thread.sleep(2000);
		
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
		subscriber.setCallback(new SimpleMqttCallBack());
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

//    @Test
//    public void testPublishMessages() throws MqttException, InterruptedException {
//    	
//    	String login = TEST_LOGIN;
//    	
//        String topicName = "command/" + login;
//        String publisherId = UUID.randomUUID().toString();
//        MqttClient client = new MqttClient("tcp://localhost:1884", publisherId);
//
//        MqttConnectOptions connOpts = new MqttConnectOptions();
//        connOpts.setCleanSession(true); //no persistent session
//        connOpts.setKeepAliveInterval(1000);
//        connOpts.setUserName(login);
//
//        connOpts.setPassword(TEST_PASSWORD.toCharArray());
//
//        client.connect(connOpts);
//
//        MqttTopic topic1 = client.getTopic(topicName);
//
//        MqttMessage message = new MqttMessage();
//        message.setQos(0);
//        message.setRetained(false);
//
//        String data = JacksonUtils.getJson(new Command().setName("test command"));
//        message.setPayload(data.getBytes());
//
//        for (int i = 0; i < 10; i++) {
//            topic1.publish(message);
//            Thread.sleep(1000);
//        }
//        client.disconnect();
//    }
//
//
//    @Test
//    public void connectionTest() throws MqttException, InterruptedException {
//
//        String topicName = "command/topic";
//
//        String publisherId = UUID.randomUUID().toString();
//
//        MqttClient client = new MqttClient("tcp://127.0.0.1:1884", publisherId);
//
//        MqttConnectOptions connOpts = new MqttConnectOptions();
//        connOpts.setCleanSession(true); //no persistent session
//        connOpts.setKeepAliveInterval(10000);
//        connOpts.setUserName(TEST_LOGIN);
//        connOpts.setPassword(TEST_PASSWORD.toCharArray());
//
//        client.connect(connOpts);
//
//        MqttTopic topic1 = client.getTopic(topicName);
//
//        MqttMessage message = new MqttMessage();
//        message.setQos(1);
//        message.setRetained(false);
//        message.setPayload("Hello world from Java".getBytes());
//
//        topic1.publish(message);
//
//        Thread.sleep(1000);
//
//        client.disconnect();
//
//        Thread.sleep(1000);
//
//        client = new MqttClient("tcp://127.0.0.1:1884", MqttClient.generateClientId());
//        client.setCallback(new SimpleMqttCallBack());
//        client.connect(connOpts);
//        client.subscribe(topicName);
//
//        Thread.sleep(1000);
//        client.disconnect();
//    }

}
