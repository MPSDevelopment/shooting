package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mpsdevelopment.plasticine.commons.DateUtils;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.AccountTypeEnum;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.config.IpscConstants;
import tech.shooting.ipsc.security.TokenUtils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

@Slf4j
@Tag(IpscConstants.UNIT_TEST_TAG)
public class TokenUtilsTest {

	@Autowired
	private TokenUtils tokenUtils = new TokenUtils();

	private Token.TokenType tokenType = Token.TokenType.USER;

	private Token.TokenType tokenTypeLostPassword = Token.TokenType.LOST_PASSWORD;

	private RoleName role = RoleName.ADMIN;

	private Long userId = 123L;

	private String server = "dev.avisionrobotics.com";

	private String userLogin = "12345678909";

	private Long organizationId = 1234567890l;

	private AccountTypeEnum accountType = AccountTypeEnum.PRO;

	public TokenUtilsTest() {

	}

	@Test
	public void checkCreation() {
		String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, DateUtils.getNextMidnight(new Date()), null);
		log.info("Token is %s", token);
	}

	@Test
	public void checkDecodeWrong() {
		assertThrows(SignatureVerificationException.class, () -> {
			String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, DateUtils.getNextMidnight(new Date()), null);
			log.info("Token is %s", token);
			assertEquals(userLogin, tokenUtils.getLoginFromToken(token + "sdsd"));
		});

	}

	@Test
	public void checkDecode() {
		String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, DateUtils.getNextMidnight(new Date()), null);
		log.info("Token is %s", token);
		assertEquals(server, tokenUtils.getServerFromToken(token));
		assertEquals(userLogin, tokenUtils.getLoginFromToken(token));
		assertEquals(tokenType, tokenUtils.getTypeFromToken(token));
		assertEquals(role, tokenUtils.getRoleFromToken(token));
		assertEquals(userId, tokenUtils.getIdFromToken(token));
		assertEquals(accountType, tokenUtils.getAccountFromToken(token));

		token = tokenUtils.createToken(userId, tokenTypeLostPassword, server, userLogin, role, accountType, DateUtils.getNextMidnight(new Date()), null);

		assertEquals(userLogin, tokenUtils.getLoginFromToken(token).toString());
		assertEquals(tokenTypeLostPassword, tokenUtils.getTypeFromToken(token));
	}

	@Test
	public void checkExpiration() throws InterruptedException {
		assertThrows(TokenExpiredException.class, () -> {
			String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, DateUtils.removeTimePart(new Date()), null);
			log.info("Token is %s", token);
			String decodedUid = tokenUtils.getLoginFromToken(token);
			assertEquals(userLogin, decodedUid);
		});
	}

	@Test
	public void checkVerifyToken() throws InterruptedException {
		String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, DateUtils.removeTimePart(new Date()), null);
		log.info("Token is %s", token);
		assertFalse(tokenUtils.verifyToken(token));

		token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, new Date(), null);
		assertTrue(tokenUtils.verifyToken(token));
		assertTrue(tokenUtils.verifyToken(server, token));
		assertTrue(tokenUtils.verifyToken("https://" + server + ":8080", token));
		assertFalse(tokenUtils.verifyToken("test.avisionrobotics.com", token));
	}

	@Test
	public void checkNotBefore() {
		assertThrows(InvalidClaimException.class, () -> {
			String token = tokenUtils.createToken(userId, tokenType, server, userLogin, role, accountType, null, DateUtils.getNextMidnight(new Date()));
			log.info("Token is %s", token);
			String decodedUid = tokenUtils.getLoginFromToken(token).toString();
			log.info("Decoded uid is %s", decodedUid);
		});
	}
}
