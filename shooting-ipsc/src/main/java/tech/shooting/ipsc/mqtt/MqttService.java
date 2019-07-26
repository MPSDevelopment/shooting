package tech.shooting.ipsc.mqtt;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.enums.WorkspaceStatusEnum;
import tech.shooting.ipsc.mqtt.event.CompetitionUpdatedEvent;
import tech.shooting.ipsc.mqtt.event.MqttOnConnectEvent;
import tech.shooting.ipsc.mqtt.event.MqttOnConnectionLostEvent;
import tech.shooting.ipsc.mqtt.event.MqttOnDisconnectEvent;
import tech.shooting.ipsc.pojo.WorkSpace;
import tech.shooting.ipsc.service.WorkSpaceService;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttDisconnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.moquette.broker.ClientDescriptor;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.interception.InterceptHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MqttService {

	private static final String MQTT_HOST = "host";

	private static final String MQTT_PORT = "port";

	@Autowired
	private IpscMqttSettings mqttSettings;

	@Autowired
	private WorkSpaceService workspaceService;

	private static Server mqttBroker;

	private MqttClient publisher;

	private IConfig classPathConfig;

	public MqttService() {
		EventBus.subscribe(this);
	}

	public void startBroker(String settingsFile) {
		startBroker(settingsFile, null);
	}

	public void startBroker(String settingsFile, Integer port) {

		if (mqttBroker != null) {
			return;
		}

		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		classPathConfig = StringUtils.isNotBlank(settingsFile) ? new ResourceLoaderConfig(classpathLoader, settingsFile) : new ResourceLoaderConfig(classpathLoader);

		if (port != null) {
			log.info("Changed mqtt port from %s to %s", classPathConfig.getProperty(MQTT_PORT), port);
			classPathConfig.setProperty(MQTT_PORT, port.toString());
		}

		mqttBroker = new Server();

		log.info("authenticator_class = %s", classPathConfig.getProperty("authenticator_class"));
		log.info("authorizator_class = %s", classPathConfig.getProperty("authorizator_class"));

//		List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener());

		try {
			// mqttBroker.startServer(classPathConfig, userHandlers);
			mqttBroker.startServer(classPathConfig);
			mqttBroker.addInterceptHandler(new MqttHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stopBroker() throws MqttException {
		if (mqttBroker == null) {
			return;
		}

		if (publisher != null) {
			publisher.disconnect();
		}

		mqttBroker.stopServer();
		log.info("Moquette Server stopped");
	}

	public MqttClient getPublisher() throws MqttException {
		if (publisher == null) {
			publisher = createPublisher(getServerUrl(), mqttSettings.getAdminLogin(), mqttSettings.getAdminPassword());
		}
		return publisher;
	}

	public String getServerUrl() {
		return "tcp://" + classPathConfig.getProperty(MQTT_HOST) + ":" + classPathConfig.getProperty(MQTT_PORT);
	}

	public MqttMessage createJsonMessage(Object object) {
		return createMessage(JacksonUtils.getJson(object));
	}

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

		var publisher = new MqttClient(url, MqttClient.generateClientId());
		publisher.connect(connOpts);
		return publisher;
	}

	public MqttClient createSubscriber(String url, String userName, String password, String... topicNames) throws MqttException {
		return createSubscriber(url, userName, password, new JsonMqttCallBack(), topicNames);
	}

	public MqttClient createSubscriber(String url, String userName, String password, MqttCallback callback, String... topicNames) throws MqttException {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true); // no persistent session
		connOpts.setKeepAliveInterval(10000);
		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		var subscriber = new MqttClient(url, MqttClient.generateClientId());
		subscriber.setCallback(callback);
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

	public Collection<ClientDescriptor> getSubscribers() {
		return mqttBroker.listConnectedClients();
	}

	public ClientDescriptor getSubscriberByClientId(String clientId) {
		for (var subscriber : getSubscribers()) {
			if (subscriber.getClientID().equals(clientId)) {
				return subscriber;
			}
		}
		return null;
	}

	@Handler
	public void handle(MqttOnConnectEvent event) throws MqttPersistenceException, MqttException {
		log.info("New connection detected %s. List of clients:", event);
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		ClientDescriptor subscriber = getSubscriberByClientId(event.getClientId());

		var workSpace = workspaceService.createWorkspace(event.getClientId(), subscriber == null ? null : subscriber.getAddress());

		getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, createJsonMessage(workSpace));
	}

	@Handler
	public void handle(MqttOnConnectionLostEvent event) throws MqttPersistenceException, MqttException {
		log.info("Connection lost detected. List of clients:", event);
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		var workSpace = workspaceService.removeWorkspace(event.getClientId());

		getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, createJsonMessage(workSpace));

	}

	@Handler
	public void handle(MqttOnDisconnectEvent event) throws MqttPersistenceException, MqttException {
		log.info("Disonnect detected. List of clients:");
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		var workSpace = workspaceService.removeWorkspace(event.getClientId());

		getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, createJsonMessage(workSpace));
	}

	@Handler
	public void handle(CompetitionUpdatedEvent event) {
		try {
			getPublisher().publish(MqttConstants.COMPETITION_TOPIC, createJsonMessage(event));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}
}
