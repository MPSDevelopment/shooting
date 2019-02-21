package tech.shooting.ipsc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
@Configuration
@ConfigurationProperties(prefix = "shooting.ipsc")
public class IpscSettings {

	private String frontendFolder;
}
