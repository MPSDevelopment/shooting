package tech.shooting.ipsc.rabbitmq.mqtt;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.ipsc.rabbitmq.event.MqttSimpleEvent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
@Component
public class SimpleMqttCallBack implements MqttCallback {


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

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        try {
            String topicName = s;
            String json = new String(mqttMessage.getPayload());

            log.info(" Some string = %s  \n\t  Message payload :\n\t %s  ", topicName, json);

            // Send event for Save current position of Drone in DataBase through DroneService
            EventBus.publishEvent(new MqttSimpleEvent(topicName, json));
        } catch (Exception e) {
            log.error("Error while got MQTT Message - %s ", e);
        }

    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("IMqttDeliveryToken to MQTT broker lost!");
    }
}