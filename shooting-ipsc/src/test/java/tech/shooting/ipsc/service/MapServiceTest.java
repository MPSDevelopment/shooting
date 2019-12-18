package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mpsdevelopment.plasticine.commons.IdGenerator;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.repository.PersonRepository;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { ImageService.class, TileService.class, MapService.class })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, WebMvcAutoConfiguration.class })
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class MapServiceTest {
	
	private static final String FILENAME = "files/Hawaii.png";
	
	private static final String MAP_FILENAME = "files/Map.jpg";

	@Autowired
	private MapService mapService;
	
	private String filename;
	
	@BeforeEach
	public void before() {
		filename = String.valueOf(IdGenerator.nextId()) + ".png";
	}

	@Test
	public void saveMapPng() throws IOException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
		mapService.saveMap(multipartFile, filename);
		
		mapService.clearMap(filename);
	}
	
	@Test
	public void saveMapJpg() throws IOException {
		MockMultipartFile multipartFile = new MockMultipartFile(MAP_FILENAME, new FileInputStream(FILENAME));
		mapService.saveMap(multipartFile, filename);
		
		mapService.clearMap(filename);
	}

	@Test
	public void getExtension() throws IOException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, FILENAME, "image/png", new FileInputStream(FILENAME));
		log.info("original file name is %s", multipartFile.getOriginalFilename());
		assertEquals("png", mapService.getExtension(multipartFile));
	}
}
