package tech.shooting.ipsc.mqtt;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.event.MqttSimpleEvent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
@Component
public class JsonMqttCallBack implements MqttCallback {


    @PostConstruct
    private void init() {
        EventBus.subscribe(this);
    }

    @PreDestroy
    private void destroy() {
        EventBus.unsubscribe(this);
    }

    public void connectionLost(Throwable throwable) {
        log.info("Connection to MQTT broker lost!");
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

        try {
        	
            String json = new String(mqttMessage.getPayload());

            log.info(" Topic : %s  Message payload : %s  ", topic, json);

            // Send event for Save current position of Drone in DataBase through DroneService
            EventBus.publishEvent(new MqttSimpleEvent(topic, json));
        } catch (Exception e) {
            log.error("Error while got MQTT Message - %s ", e);
        }

    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("IMqttDeliveryToken to MQTT broker lost!");
    }
}