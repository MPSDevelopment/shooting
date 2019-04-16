package tech.shooting.ipsc.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.bean.UploadFileBean;
import tech.shooting.ipsc.config.AppConfig;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.pojo.Address;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.CheckinRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;
import tech.shooting.ipsc.service.ImageService;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CheckinRepository.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMongoConfig.class, AppConfig.class, TokenUtils.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, TokenAuthenticationManager.class,
		TokenAuthenticationFilter.class, IpscUserDetailsService.class, ImageService.class, ImageController.class })
public class ImageControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private ImageService imageService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	private User user;

	private User admin;

	private String userToken;

	private String adminToken;

	private File file = new File("files/logo.png");

	private MockMultipartFile uploadFile;

	private Tika tika = new Tika();

	@BeforeEach
	public void before() throws FileNotFoundException, IOException {

		uploadFile = new MockMultipartFile("file", file.getName(), tika.detect(file), new FileInputStream(file));

		user = new User().setLogin(RandomStringUtils.randomAlphanumeric(15)).setName("Test firstname").setPassword("8523").setRoleName(RoleName.USER).setAddress(new Address().setIndex("08150"));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		userToken = adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.USER, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
	}

	@Test
	public void checkPostImage() throws Exception {

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

//		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//		
		// try access admin role
		String result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0).file(uploadFile).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		log.info("Image post result is %s", result);

		UploadFileBean bean = JacksonUtils.fromJson(UploadFileBean.class, result);

		assertNotNull(bean.getPath());
	}

	@Test
	public void checkPostImageByFilename() throws Exception {

		String id = "123";

		// try access with unauthorized user
		mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id).file(uploadFile)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

//		// try access with user role
		mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id).file(uploadFile).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
//		
		// try access admin role
		String result = mockMvc.perform(MockMvcRequestBuilders.multipart(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id).file(uploadFile).header(Token.TOKEN_HEADER, adminToken))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		log.info("Image post result is %s", result);

		UploadFileBean bean = JacksonUtils.fromJson(UploadFileBean.class, result);

		assertEquals(id, bean.getPath());
	}

	@Test
	public void checkGetImage() throws Exception {

		String id = RandomStringUtils.randomNumeric(10);

//		// try access with unauthorized user
//		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
//		// try access with non admin user
//		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin user but not existed image
		mockMvc.perform(MockMvcRequestBuilders.get((ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isNotFound());

		imageService.storeFile(uploadFile, id);

		// try access with admin user with existed image
//		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/" + id).header(Token.TOKEN_HEADER, adminToken).accept(MediaType.IMAGE_JPEG_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());

	}
	
	@Test
	public void checkGetImageData() throws Exception {
		String id = RandomStringUtils.randomNumeric(10);
		// try access with unauthorized user
//		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA.replace(ControllerAPI.REQUEST_ID, id))).andExpect(MockMvcResultMatchers.status().isUnauthorized());
//		// try access with non admin user
//		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA.replace(ControllerAPI.REQUEST_ID, id)).header(Token.TOKEN_HEADER, userToken)).andExpect(MockMvcResultMatchers.status().isForbidden());
		// try access with admin user but not existed image
		mockMvc.perform(MockMvcRequestBuilders.get((ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA.replace(ControllerAPI.REQUEST_ID, id))).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	

		imageService.storeFile(uploadFile, id);

		// try access with admin user with existed image
		mockMvc.perform(MockMvcRequestBuilders.get(ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA.replace(ControllerAPI.REQUEST_ID, id)).header(Token.TOKEN_HEADER, adminToken)).andExpect(MockMvcResultMatchers.status().isOk());
		
	}

}
