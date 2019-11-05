package tech.shooting.ipsc;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.bean.TokenLogin;
import tech.shooting.ipsc.bean.UserLogin;
import tech.shooting.ipsc.controller.ControllerAPI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

@Slf4j
public class IntergationTest {

	private static final String serverUrl = "http://52.176.90.219:9901";

	protected static final String ADMIN_LOGIN = "admin";

	protected static final String ADMIN_PASSWORD = "test";

	private UserLogin userLogin;
	private String rightUserJson;

	private String token;

	@BeforeEach
	public void beforeEach() {
		userLogin = new UserLogin().setLogin(ADMIN_LOGIN).setPassword(ADMIN_PASSWORD);
		rightUserJson = JacksonUtils.getFullJson(userLogin);
		token = getToken();
	}

	@Test
	public void getStatus() {
		RestAssured.given().header(Token.TOKEN_HEADER, token).get(serverUrl + ControllerAPI.AUTH_CONTROLLER + ControllerAPI.AUTH_CONTROLLER_GET_STATUS).then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void getTokenTimes() {
		IntStream range = IntStream.rangeClosed(1, 100).parallel();
		range.forEach(item -> {
			getToken();
		});
	}

	private String getToken() {
		return getToken(rightUserJson);
	}

	private String getToken(String userJson) {
		long t1 = System.currentTimeMillis();
		// login to the system and get token
		String token = RestAssured.given().header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE).body(userJson).when()
				.post(serverUrl + ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN).then().extract().body().jsonPath().getString(TokenLogin.TOKEN_FIELD);
		long t2 = System.currentTimeMillis();
		assertTrue(StringUtils.isNotBlank(token));
		// token = token.replaceAll("\"", "");
		log.info("Token taken for %s is %s", t2 - t1, token);
		return token;
	}
}
