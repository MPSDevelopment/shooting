package tech.shooting.ipsc.rabbitmq.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class MqttRabbitService {

	public static final String IPSC_TOPIC = "ipsc/";

	@Value("${spring.rabbitmq.username}")
	private String admLogin;

	@Value("${spring.rabbitmq.password}")
	private String admPsw;

	// - "tcp://127.0.0.1:1883"
	@Value("${ipsc.rabbitmq.mqttUrl}")
	private String mqttUrl;

	@Autowired(required = true)
	private SimpleMqttCallBack simpleMqttCallBack;

	private MqttClient adminClient = null;

	@PostConstruct
	private void init() {
		log.info("Init MQTT Client");
		adminConnect();
		adminSubscribeOnTopic(IPSC_TOPIC.concat("#"));
	}

	@PreDestroy
	private void destroy() {
		log.info("Close MQTT Client");
		adminDisconnect();
	}

	private void adminConnect() {
		try {
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true); // no persistent session
			connOpts.setKeepAliveInterval(1000);
			connOpts.setUserName(admLogin);
			connOpts.setPassword(admPsw.toCharArray());
			connOpts.setAutomaticReconnect(true);

			adminClient = new MqttClient(mqttUrl, MqttClient.generateClientId());

			adminClient.setCallback(simpleMqttCallBack);
			adminClient.connect(connOpts);

		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void adminSubscribeOnTopic(String nameTopic) {
		if (StringUtils.isBlank(nameTopic)) {
			log.error("Error - You are try SUBSCRIBE MQTT Client on Empty TOPIC");
			return;
		}
		if (adminClient == null) {
			log.info("MQTT Client is NULL, try reconnect");
			adminConnect();
		}
		if (adminClient != null) {
			try {
				adminClient.subscribe(nameTopic);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			log.error("Error - We can not reconnect MQTT Client");
		}
	}

	private void publishMessage(String nameTopic, int qos, String json) {
		if (StringUtils.isBlank(nameTopic)) {
			log.error("Error - You are try SEND MQTT Message on Empty TOPIC");
			return;
		}

		if (adminClient == null) {
			log.info("MQTT Client is NULL, try reconnect");
			adminConnect();
		}

		if (adminClient != null) {
			MqttTopic topic = adminClient.getTopic(nameTopic);

			MqttMessage message = new MqttMessage();
			message.setQos(qos);
			message.setRetained(true);
			message.setPayload(json.getBytes());

			try {
				topic.publish(message);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

	private void adminDisconnect() {
		if (adminClient != null) {
			try {
				adminClient.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

}
