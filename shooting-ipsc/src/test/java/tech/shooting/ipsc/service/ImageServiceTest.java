package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;
import tech.shooting.ipsc.pojo.Image;
import tech.shooting.ipsc.repository.PersonRepository;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = PersonRepository.class)
@ContextConfiguration(classes = { ImageService.class })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, WebMvcAutoConfiguration.class })
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class ImageServiceTest {

	private static final String FILENAME = "files/Hawaii.png";

	@Autowired
	public ImageService imageService;

	private String filename;

	@BeforeEach
	public void before() {
		filename = String.valueOf(IdGenerator.nextId());
	}

	@Test
	public void storeFile() throws FileNotFoundException, IOException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
		Image image = imageService.storeFile(multipartFile, filename);

		assertNotNull(image);
	}

	@Test
	public void storeCircularFile() throws FileNotFoundException, IOException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
		Image image = imageService.storeCircularFile(multipartFile, filename);

		assertNotNull(image);
	}

	@Test
	public void getImageByFilename() throws FileNotFoundException, IOException, BadRequestException, NotFoundException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
		Image image = imageService.storeFile(multipartFile, filename);

		Image loadedImage = imageService.getImageByFilename(filename);

		assertEquals(image.getId(), loadedImage.getId());
	}

	@Test
	public void deleteFile() throws FileNotFoundException, IOException, BadRequestException, NotFoundException {
		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
		Image image = imageService.storeFile(multipartFile, filename);

		imageService.deleteFile(filename);
		assertThrows(NotFoundException.class, () -> {
			imageService.getImageByFilename(filename);
		});
	}

//	@Test
//	public void getFindFile() throws FileNotFoundException, IOException, BadRequestException {
//		MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, new FileInputStream(FILENAME));
//		Image image = imageService.storeFile(multipartFile, filename);
//
//		var loadedImage = imageService.findFile(filename);
//		
//		assertEquals(MediaType.IMAGE_PNG_VALUE, loadedImage.get().getMediaType().get());
//	}

}
