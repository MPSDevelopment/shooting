package tech.shooting.ipsc.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.Token.TokenType;
import tech.shooting.commons.pojo.TokenUser;

@Slf4j
@Component
public class TokenUtils {

	private JWTVerifier verifier;

	private Algorithm algorithm;

	private static final String ACCESS_TOKEN = "accessToken";

	private static String SECRET_CODE = "q235asdg4aeayffewf";

	private static String ISSUER = "Avision";

	public TokenUtils() {
		algorithm = Algorithm.HMAC256(SECRET_CODE);
		verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
	}

	public TokenUser getByToken(String token) {
		if (token != null && !token.isEmpty()) {
			try {
				TokenUser user = new TokenUser();
				user.setEmail(getLoginFromToken(token));
				user.setId(getIdFromToken(token));
				user.setRoleName(getRoleFromToken(token));
				return user;
			} catch (InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
				log.error("Cannot get a user by token %s because %s", token, e.getMessage());
			}
		}
		return null;
	}

	public String createToken(Long userId, TokenType tokenType, String server, String userLogin, RoleName roleName, Date expirationDate, Date notBeforeDate) {
		if (server != null) {
			server = server.replace("https://", "").replace("http://", "").toLowerCase();
		}

		Map<String, Object> authPayload = new HashMap<String, Object>();
		authPayload.put(Token.FIELD_ID, userId);
		authPayload.put(Token.FIELD_SERVER, server);
		authPayload.put(Token.FIELD_LOGIN, userLogin);
		authPayload.put(Token.FIELD_TYPE, tokenType);
		authPayload.put(Token.FIELD_ROLE, roleName);
		String token = JWT.create().withIssuer(ISSUER).withIssuedAt(new Date()).withExpiresAt(expirationDate).withNotBefore(notBeforeDate).withHeader(authPayload).sign(algorithm);

		return token;
	}

	// public String createToken(Long userId, TokenType tokenType, String userLogin, RoleName roleName, Long organizationId, Date expirationDate, Date notBeforeDate) {
	// Map<String, Object> authPayload = new HashMap<String, Object>();
	// authPayload.put(Token.FIELD_ID, userId);
	// authPayload.put(Token.FIELD_LOGIN, userLogin);
	// authPayload.put(Token.FIELD_TYPE, tokenType.toString());
	// authPayload.put(Token.FIELD_ROLE, roleName.toString());
	// authPayload.put(Token.FIELD_ORGANIZATION, organizationId);
	//
	// TokenOptions tokenOptions = new TokenOptions();
	// tokenOptions.setAdmin(true);
	// tokenOptions.setExpires(expirationDate);
	// tokenOptions.setNotBefore(notBeforeDate);
	//
	// TokenGenerator tokenGenerator = new TokenGenerator(SECRET_CODE);
	// String token = tokenGenerator.createToken(authPayload, tokenOptions);
	//
	// return token;
	// }

	public String getLoginFromToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}

		return verifier.verify(token).getHeaderClaim(Token.FIELD_LOGIN).asString();
	}

	public String getServerFromToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}

		return verifier.verify(token).getHeaderClaim(Token.FIELD_SERVER).asString();
	}

	public Long getIdFromToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return verifier.verify(token).getHeaderClaim(Token.FIELD_ID).asLong();
	}

	public TokenType getTypeFromToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return verifier.verify(token).getHeaderClaim(Token.FIELD_TYPE).as(TokenType.class);
	}

	public RoleName getRoleFromToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return verifier.verify(token).getHeaderClaim(Token.FIELD_ROLE).as(RoleName.class);
	}

	/**
	 * Verifies is a token valid
	 * 
	 * @param token
	 * @return
	 */
	public boolean verifyToken(String token) {
		return verifyToken(null, token);
	}

	/**
	 * Verifies is a token valid using also a server comparison
	 * 
	 * @param token
	 * @return
	 */
	public boolean verifyToken(String currentServer, String token) {
		if (StringUtils.isBlank(token)) {
			log.debug("Payload isBlank = %s", token);
			return false;
		}
		try {
			DecodedJWT decoded = verifier.verify(token);
			Date expirationDate = decoded.getClaim(PublicClaims.EXPIRES_AT).asDate();
			// log.info("Token expiration is %s", expirationDate);

			String server = getServerFromToken(token);
			
			if (server == null) {
				log.error("Token server is null, need to regenerate the token");
				return false;
			}
			
			if (currentServer != null && !currentServer.toLowerCase().contains(server)) {
				log.error("Token server %s does not match current server %s", server, currentServer);
				return false;
			}

			return true;
		} catch (JWTVerificationException e) {
			log.error("Error Verification = %s", e.getMessage());
			return false;
		}
	}

	public static String getTokenFromRequestCookies(HttpServletRequest servletRequest) {
		for (Cookie cookie : servletRequest.getCookies()) {
			if (cookie.getName().equals(ACCESS_TOKEN)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
