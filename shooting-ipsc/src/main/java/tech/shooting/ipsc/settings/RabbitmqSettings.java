package tech.shooting.ipsc.settings;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configuration
@ToString
@Slf4j
@ConfigurationProperties(prefix = "ipsc.rabbitmq")
public class RabbitmqSettings {

    private String apiUrl;

    private String mqttUrl;

    private String ipscQueueName;

}
