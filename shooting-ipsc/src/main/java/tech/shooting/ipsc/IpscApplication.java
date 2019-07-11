package tech.shooting.ipsc;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.mqtt.MqttService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableMongoRepositories
@EntityScan(basePackages = { "tech.shooting.ipsc" })
@ComponentScan(basePackages = { "tech.shooting.commons.spring", "tech.shooting.commons.utils", "tech.shooting.ipsc" })
@EnableScheduling
@Slf4j
public class IpscApplication {

	@Autowired
	public MqttService mqttService;

	public static void main(String[] args) throws IOException {
		log.info("Starting IpscApplication");
		SpringApplication application = new SpringApplication(IpscApplication.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		application.run(args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			mqttService.startBroker(null);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					mqttService.stopBroker();
				}
			});
		};
	}

}
