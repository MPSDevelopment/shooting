package tech.shooting.speedway;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.speedway.service.TagService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.impinj.octane.OctaneSdkException;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class IpscApplication {

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

			tagService.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {

						tagService.stop();

					} catch (OctaneSdkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		};
	}

}
