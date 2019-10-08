package tech.shooting.speedway;

import lombok.extern.slf4j.Slf4j;
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

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.TagOpCompleteListener;
import com.impinj.octane.TagOpReport;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class IpscApplication {

	static ImpinjReader impinjReader;

	public static void main(String[] args) throws IOException {
		log.info("Starting IpscApplication");
		SpringApplication application = new SpringApplication(IpscApplication.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		application.run(args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			impinjReader = new ImpinjReader();

			impinjReader.connect("192.168.31.212");

			log.info("Reader has been connected");

			impinjReader.applyDefaultSettings();

			impinjReader.start();

			log.info("Reader has been started");

			impinjReader.setTagOpCompleteListener(new TagOpCompleteListener() {

				@Override
				public void onTagOpComplete(ImpinjReader arg0, TagOpReport report) {
					log.info("Tag report %s", report.getResults());
				}
			});

			impinjReader.setTagReportListener(new TagReportListener() {

				@Override
				public void onTagReported(ImpinjReader arg0, TagReport report) {
					log.info("Tag report %s", report.getTags());
				}
			});

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						impinjReader.stop();

						log.info("Reader has been stopped");

						impinjReader.disconnect();

						log.info("Reader has been disconnected");

					} catch (OctaneSdkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		};
	}

}
