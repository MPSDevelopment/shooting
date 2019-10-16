package tech.shooting.ipsc.mqtt;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.service.MqttService;

@Service
@Slf4j
public class MqttExtension implements BeforeAllCallback, AfterAllCallback {
	
	@Autowired
	private MqttService mqttService;

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		log.info("Stopping mqtt server");
		mqttService.stopBroker();
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		log.info("Starting mqtt server");
		mqttService.startBroker("config/moquette.conf");
	}

}