package tech.shooting.ipsc.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.advice.ValidationErrorHandler;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.config.IpscMqttSettings;
import tech.shooting.ipsc.config.IpscSettings;
import tech.shooting.ipsc.config.SecurityConfig;
import tech.shooting.ipsc.controller.CompetitionController;
import tech.shooting.ipsc.controller.ControllerAPI;
import tech.shooting.ipsc.db.DatabaseCreator;
import tech.shooting.ipsc.db.UserDao;
import tech.shooting.ipsc.enums.*;
import tech.shooting.ipsc.mqtt.MqttConstants;
import tech.shooting.ipsc.mqtt.MqttService;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.RankRepository;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.CompetitionService;

import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
@ContextConfiguration(classes = { ValidationErrorHandler.class, IpscSettings.class, IpscMqttSettings.class, IpscMongoConfig.class, SecurityConfig.class, UserDao.class, DatabaseCreator.class, CompetitionController.class,
		CompetitionService.class, MqttService.class })
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class MqttLogicTest {

	@Autowired
	private IpscMqttSettings settings;

	@Autowired
	private MqttService mqttService;

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private RankRepository rankRepository;

	private User admin;

	private User judge;

	private Competition testingCompetition;

	private String adminToken;

	private String judgeToken;

	private Rank rank;

	private int messageCount;

	private MqttClient subscriber;

	@BeforeEach
	public void beforeEach() {
		competitionRepository.deleteAll();
		rank = rank == null ? rankRepository.save(new Rank().setRus("бла бла бла").setKz("major").setOfficer(true)) : rank;
		testingCompetition = competitionRepository.save(new Competition().setName("Test name Competition").setQualifierRank(ClassificationBreaks.D).setClazz(CompetitionClassEnum.LEVEL_1));
		admin = userRepository.findByLogin(DatabaseCreator.ADMIN_LOGIN);
		judge = userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN) == null ? userRepository.save(new User().setLogin("judge").setRoleName(RoleName.JUDGE).setName("judge_name").setPassword(RandomStringUtils.randomAscii(14)))
				: userRepository.findByLogin(DatabaseCreator.JUDGE_LOGIN);
		adminToken = tokenUtils.createToken(admin.getId(), Token.TokenType.USER, admin.getLogin(), RoleName.ADMIN, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		judgeToken = tokenUtils.createToken(judge.getId(), Token.TokenType.USER, judge.getLogin(), RoleName.JUDGE, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));

		mqttService.startBroker("config/moquette.conf");
	}

	@AfterEach
	public void afterEach() throws MqttException {
		mqttService.stopBroker();
	}

//	@Test
	public void checkMqtt() throws Exception {

		messageCount = 0;
		subscriber = createSubscriber();

		Competition test = competitionRepository.findByName(testingCompetition.getName());
		// try access to start competition with judge
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_START.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access to stop competition with judge
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_STOP.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access to start competition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_START.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

		// try access to stop competition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_STOP.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

		subscriber.disconnect();
		subscriber.close(true);

		assertEquals(4, messageCount);

		// try access to start competition with judge
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_START.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, judgeToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());

		subscriber = createSubscriber();

		// try access to stop competition with authorized admin
		mockMvc.perform(MockMvcRequestBuilders
				.post(ControllerAPI.COMPETITION_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION_STOP.replace(ControllerAPI.REQUEST_COMPETITION_ID, test.getId().toString()))
				.header(Token.TOKEN_HEADER, adminToken).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());
		
		Thread.sleep(200);
		
		subscriber.disconnect();
		subscriber.close(true);

		assertEquals(5, messageCount);

	}

	private MqttClient createSubscriber() throws MqttException {
		return mqttService.createSubscriber(mqttService.getServerUrl(), settings.getGuestLogin(), settings.getGuestPassword(), new MqttCallback() {

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				log.info("Message arrived: %s", new String(message.getPayload()));
				messageCount++;
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
			}

			@Override
			public void connectionLost(Throwable cause) {
			}
		}, MqttConstants.COMPETITION_TOPIC);
	}
}
