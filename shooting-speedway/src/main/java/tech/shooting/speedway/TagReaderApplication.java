package tech.shooting.speedway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.impinj.octane.ImpinjReader;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class TagReaderApplication implements CommandLineRunner {

	ImpinjReader impinjReader;

	public static void main(String[] args) {
		SpringApplication.run(TagReaderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Reading imping reader");
		impinjReader = new ImpinjReader();
	}
}