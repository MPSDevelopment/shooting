package tech.shooting.ipsc.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@Configuration
@ConfigurationProperties(prefix = "ipsc.mqtt")
public class IpscMqttSettings {
	
	private String guestLogin = "guest";

	private String guestPassword = "guest";
	
	private String adminLogin;
	
	private String adminPassword;
	
	private String ipscTopicName;
}
