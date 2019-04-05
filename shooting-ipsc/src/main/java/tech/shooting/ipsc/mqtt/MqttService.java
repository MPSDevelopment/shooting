package tech.shooting.ipsc.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class MqttService {

	public static final String IPSC_TOPIC = "ipsc/";

	@Value("${spring.rabbitmq.username}")
	private String admLogin;

	@Value("${spring.rabbitmq.password}")
	private String admPsw;

	public MqttMessage createMessage(String payload) {
		MqttMessage message = new MqttMessage();
		message.setQos(1);
		message.setRetained(false);
		message.setPayload(payload.getBytes());
		return message;
	}

	public MqttClient createPublisher(String url, String userName, String password) throws MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		String publisherId = UUID.randomUUID().toString();
		var publisher = new MqttClient(url, publisherId);
		publisher.connect(connOpts);
		return publisher;
	}

	public MqttClient createSubscriber(String url, String userName, String password, String... topicNames) throws MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		var subscriber = new MqttClient(url, MqttClient.generateClientId());
		subscriber.setCallback(new JsonMqttCallBack());
		subscriber.connect(connOpts);
		Arrays.asList(topicNames).forEach(topicName -> {
			try {
				subscriber.subscribe(topicName);
			} catch (MqttException e) {
				log.error("Cannot connect to topic: %s", topicName);
			}
		});

		return subscriber;
	}

}
