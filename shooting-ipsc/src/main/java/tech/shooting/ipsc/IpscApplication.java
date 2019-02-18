package tech.shooting.ipsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableWebMvc
@EnableMongoRepositories
@EntityScan(basePackages = { "tech.shooting.ipsc" })
@ComponentScan(basePackages = { "tech.shooting.ipsc" })
@EnableScheduling
@Slf4j
public class IpscApplication {

	public static void main(String[] args) {
		log.info("Starting IpscApplication");
		SpringApplication application = new SpringApplication(IpscApplication.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		application.run(args);
	}
}
