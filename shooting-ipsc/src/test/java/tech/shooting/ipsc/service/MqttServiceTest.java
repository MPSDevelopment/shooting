package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.JacksonUtils;
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

	@Test
	public void testPublishSubscribe() throws MqttException, InterruptedException {

		String topicName = "command/topic";
		
		// subscriber

		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(TEST_LOGIN);
		connOpts.setPassword(TEST_PASSWORD.toCharArray());

		var subscriber = new MqttClient("tcp://127.0.0.1:1884", MqttClient.generateClientId());
		subscriber.setCallback(new SimpleMqttCallBack());
		subscriber.connect(connOpts);
		subscriber.subscribe(topicName);
		
		// publisher 
		
		String publisherId = UUID.randomUUID().toString();
		var publisher = new MqttClient("tcp://localhost:1884", publisherId);
		publisher.connect(connOpts);
		
		// publish a message

		MqttTopic topic1 = publisher.getTopic(topicName);

		MqttMessage message = new MqttMessage();
		message.setQos(1);
		message.setRetained(false);
		message.setPayload("Hello world from Java".getBytes());

		topic1.publish(message);

		Thread.sleep(1000);
		subscriber.disconnect();
		
		

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
