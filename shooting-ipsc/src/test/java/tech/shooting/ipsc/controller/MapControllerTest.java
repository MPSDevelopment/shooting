package tech.shooting.ipsc.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.UploadFileBean;
import tech.shooting.ipsc.bean.UploadMapBean;
import tech.shooting.ipsc.config.AppConfig;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.ImageService;
import tech.shooting.ipsc.service.MapService;
import tech.shooting.ipsc.service.TileService;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@EnableWebMvc
@SpringBootTest
@DirtiesContext
@Slf4j
//@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, AppConfig.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, MapService.class, ImageService.class, TileService.class, MapController.class })
public class MapControllerTest extends BaseControllerTest {

	private MockMultipartFile uploadFile;

	private File file = new File("files/logo.png");

	private Tika tika = new Tika();

	@Autowired
	private MapService mapService;

	private UploadMapBean bean;

	@Test
	public void checkPostImage() throws Exception {

		uploadFile = new MockMultipartFile("file", file.getName(), tika.detect(file), new FileInputStream(file));

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

//		// try access with user role
		String result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		bean = JacksonUtils.fromJson(UploadMapBean.class, result);
		mapService.clearMap(bean.getFilename());

		// try access admin role
		result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		log.info("Image post result is %s", result);
		bean = JacksonUtils.fromJson(UploadMapBean.class, result);
		mapService.clearMap(bean.getFilename());

		UploadMapBean bean = JacksonUtils.fromJson(UploadMapBean.class, result);
		assertNotNull(bean.getPath());
		assertNotNull(bean.getFilename());

		bean = JacksonUtils.fromJson(UploadMapBean.class, result);
		result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
				.getResponse().getContentAsString();

		bean = JacksonUtils.fromJson(UploadMapBean.class, result);
		mapService.clearMap(bean.getFilename());

	}

	@Test
	public void checkGetTile() throws Exception {

		uploadFile = new MockMultipartFile("file", file.getName(), tika.detect(file), new FileInputStream(file));

		String result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		log.info("Image post result is %s", result);

		UploadMapBean mapBean = JacksonUtils.fromJson(UploadMapBean.class, result);
		assertNotNull(mapBean.getPath());
		assertNotNull(mapBean.getFilename());

		// get tile after upload

		mockMvc.perform(MockMvcRequestBuilders
				.get(ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0
						+ ControllerAPI.MAP_CONTROLLER_GET_TILE_URL.replace(ControllerAPI.REQUEST_ID, mapBean.getPath()).replace(ControllerAPI.REQUEST_Z, "10").replace(ControllerAPI.REQUEST_X, "0").replace(ControllerAPI.REQUEST_Y, "0"))
				.accept(MediaType.ALL_VALUE).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());

		mapService.clearMap(mapBean.getFilename());
	}

}
