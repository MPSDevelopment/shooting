package tech.shooting.ipsc;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.service.MqttService;
import tech.shooting.ipsc.service.TagService;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;
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

import com.impinj.octane.OctaneSdkException;

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

	@Autowired
	public TagService tagService;

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

			try {
				tagService.start();
			} catch (Throwable e) {
				log.info("Cannot start tag service %s", e.getMessage());
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						mqttService.stopBroker();
					} catch (MqttException e) {
						log.error("Cannot stop Mqtt broker : %s", e.getMessage());
					}
					try {
						tagService.stop();
					} catch (OctaneSdkException e) {
						log.error("Cannot stop Tag service : %s", e.getMessage());
					}
				}
			});
		};
	}

}
