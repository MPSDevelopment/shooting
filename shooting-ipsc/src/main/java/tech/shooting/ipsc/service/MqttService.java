package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.enums.WorkspaceStatusEnum;
import tech.shooting.ipsc.event.CompetitionUpdatedEvent;
import tech.shooting.ipsc.event.MqttOnConnectEvent;
import tech.shooting.ipsc.event.MqttOnConnectionLostEvent;
import tech.shooting.ipsc.event.MqttOnDisconnectEvent;
import tech.shooting.ipsc.event.RunningOnConnectEvent;
import tech.shooting.ipsc.event.RunningOnDisconnectEvent;
import tech.shooting.ipsc.event.RunningUpdatedEvent;
import tech.shooting.ipsc.event.TestFinishedEvent;
import tech.shooting.ipsc.event.TestStartedEvent;
import tech.shooting.ipsc.event.WorkspaceChangedEvent;
import tech.shooting.ipsc.mqtt.JsonMqttCallBack;
import tech.shooting.ipsc.mqtt.MqttConstants;
import tech.shooting.ipsc.mqtt.MqttHandler;
import tech.shooting.ipsc.pojo.Workspace;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.moquette.broker.ClientDescriptor;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
@Slf4j
public class MqttService {

	private static final String MQTT_HOST = "host";

	private static final String MQTT_PORT = "port";

	@Autowired
	private IpscMqttSettings mqttSettings;

	@Autowired
	private WorkspaceService workspaceService;

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

	public boolean isPublisherExists() {
		return publisher != null;
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

		MqttClient publisher = new MqttClient(url, MqttClient.generateClientId());
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

		MqttClient subscriber = new MqttClient(url, MqttClient.generateClientId());
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
		if (mqttBroker == null) {
			return new ArrayList<>();
		}
		return mqttBroker.listConnectedClients();
	}

	public ClientDescriptor getSubscriberByClientId(String clientId) {
		for (ClientDescriptor subscriber : getSubscribers()) {
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

		Workspace workSpace = workspaceService.createWorkspace(event.getClientId(), subscriber == null ? null : subscriber.getAddress());

		MqttMessage message = createJsonMessage(workSpace);
		try {
			log.info("Sending workspace on connect");
			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, message);
		} catch (MqttException e) {
			log.error("Cannot send a message %s", message);
		}
	}

	@Handler
	public void handle(MqttOnConnectionLostEvent event) throws MqttPersistenceException, MqttException {
		log.info("Connection lost detected. List of clients:", event);
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		Workspace workSpace = workspaceService.removeWorkspace(event.getClientId());

		MqttMessage message = createJsonMessage(workSpace);
		try {
			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, message);
		} catch (MqttException e) {
			log.error("Cannot send a message %s", message);
		}

	}

	@Handler
	public void handle(MqttOnDisconnectEvent event) {
		log.info("Disonnect detected. List of clients:");
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		Workspace workSpace = workspaceService.removeWorkspace(event.getClientId());

		MqttMessage message = createJsonMessage(workSpace);
		try {
			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, message);
		} catch (MqttException e) {
			log.error("Cannot send a message %s", message);
		}
	}

	@Handler
	public void handle(WorkspaceChangedEvent event) {
		log.info("Workspace changed detected. List of clients:");
		getSubscribers().forEach(item -> log.info("Subscriber id %s ip %s", item.getClientID(), item.getAddress()));

		MqttMessage message = createJsonMessage(event.getWorspace());
		try {
			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, message);
		} catch (MqttException e) {
			log.error("Cannot send a message %s", message);
		}
	}

	@Handler
	public void handle(TestStartedEvent event) {

		if (event.getWorspace() == null) {
			log.error("Cannot send test started event without workspace");
			return;
		}

		try {
			String topic = MqttConstants.TEST_TOPIC + "/" + event.getWorspace().getIp();
			log.info("Sending test start to the topic %s", topic);
			getPublisher().publish(topic, createJsonMessage(event.getWorspace()));

			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, createJsonMessage(event.getWorspace().setScore(WorkspaceStatusEnum.STARTED_TEST.toString())));

		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

	@Handler
	public void handle(TestFinishedEvent event) {
		try {

			if (event.getScore() == null) {
				log.error("Cannot send test finished event without score");
				return;
			}

			if (event.getScore().getPerson() == null) {
				log.error("Cannot send test finished event without person");
				return;
			}

			Workspace workspace = workspaceService.getWorkspaceByQuizIdAndPersonId(event.getScore().getQuizId(), event.getScore().getPerson().getId());

			if (workspace == null) {
				log.error("Cannot send test finished event without workspace by quizid %s and personId %s", event.getScore().getQuizId(), event.getScore().getPerson().getId());
				return;
			}

			workspace.setScore(String.valueOf(event.getScore().getScore()));

			getPublisher().publish(MqttConstants.WORKSPACE_TOPIC, createJsonMessage(workspace));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

	@Handler
	public void handle(CompetitionUpdatedEvent event) {
		try {
			getPublisher().publish(MqttConstants.COMPETITION_TOPIC, createJsonMessage(event));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

	@Handler
	public void handle(RunningUpdatedEvent event) {
		try {
			getPublisher().publish(MqttConstants.RUNNING_TOPIC, createJsonMessage(event));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

	@Handler
	public void handle(RunningOnConnectEvent event) {
		try {
			getPublisher().publish(MqttConstants.RUNNING_TOPIC, createJsonMessage(event));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

	@Handler
	public void handle(RunningOnDisconnectEvent event) {
		try {
			getPublisher().publish(MqttConstants.RUNNING_TOPIC, createJsonMessage(event));
		} catch (MqttException e) {
			log.error("Cannot send a mqtt message %s", event);
		}
	}

}
