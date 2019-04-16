package tech.shooting.ipsc;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.mqtt.PublisherListener;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.interception.InterceptHandler;

@SpringBootApplication
@EnableWebMvc
@EnableMongoRepositories
@EntityScan(basePackages = { "tech.shooting.ipsc" })
@ComponentScan(basePackages = { "tech.shooting.commons.spring", "tech.shooting.commons.utils", "tech.shooting.ipsc" })
@EnableScheduling
@Slf4j
public class IpscApplication {

	private static Server mqttBroker;

	public static void main(String[] args) throws IOException {
		log.info("Starting IpscApplication");
		SpringApplication application = new SpringApplication(IpscApplication.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		application.run(args);

		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

		mqttBroker = new Server();
		
		List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener());
		mqttBroker.startServer(classPathConfig, userHandlers);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				mqttBroker.stopServer(); 
				log.info("Moquette Server stopped");
			}
		});
	}
}
