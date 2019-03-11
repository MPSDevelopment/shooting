package tech.shooting.ipsc.security;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.Token.TokenType;
import tech.shooting.commons.pojo.TokenUser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenUtils {

	private static final String ACCESS_TOKEN = "accessToken";
	private static String SECRET_CODE = "q235asdg4aeayffewf";
	private static String ISSUER = "Avision";
	private JWTVerifier verifier;
	private Algorithm algorithm;

	public TokenUtils () {
		algorithm = Algorithm.HMAC256(SECRET_CODE);
		verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
	}

	public static String getTokenFromRequestCookies (HttpServletRequest servletRequest) {
		for(Cookie cookie : servletRequest.getCookies()) {
			if(cookie.getName().equals(ACCESS_TOKEN)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public TokenUser getByToken (String token) {
		if(token != null && !token.isEmpty()) {
			try {
				TokenUser user = new TokenUser();
				user.setLogin(getLoginFromToken(token));
				user.setId(getIdFromToken(token));
				user.setRoleName(getRoleFromToken(token));
				return user;
			} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
				log.error("Cannot get a user by token %s because %s", token, e.getMessage());
			}
		}
		return null;
	}

	public String createToken (Long userId, TokenType tokenType, String userLogin, RoleName roleName, Date expirationDate, Date notBeforeDate) {

		Map<String, Object> authPayload = new HashMap<String, Object>();
		authPayload.put(Token.FIELD_ID, userId);
		authPayload.put(Token.FIELD_LOGIN, userLogin);
		authPayload.put(Token.FIELD_TYPE, tokenType);
		authPayload.put(Token.FIELD_ROLE, roleName);
		String token = JWT.create().withIssuer(ISSUER).withIssuedAt(new Date()).withExpiresAt(expirationDate).withNotBefore(notBeforeDate).withHeader(authPayload).sign(algorithm);

		return token;
	}

	public String getLoginFromToken (String token) {
		if(StringUtils.isBlank(token)) {
			return null;
		}

		return verifier.verify(token).getHeaderClaim(Token.FIELD_LOGIN).asString();
	}

	public Long getIdFromToken (String token) {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		return verifier.verify(token).getHeaderClaim(Token.FIELD_ID).asLong();
	}

	public TokenType getTypeFromToken (String token) {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		return verifier.verify(token).getHeaderClaim(Token.FIELD_TYPE).as(TokenType.class);
	}

	public RoleName getRoleFromToken (String token) {
		if(StringUtils.isBlank(token)) {
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
	public boolean verifyToken (String token) {
		if(StringUtils.isBlank(token)) {
			log.debug("Payload isBlank = %s", token);
			return false;
		}
		try {
			DecodedJWT decoded = verifier.verify(token);
			Date expirationDate = decoded.getClaim(PublicClaims.EXPIRES_AT).asDate();
			// log.info("Token expiration is %s", expirationDate);
			return true;
		} catch(JWTVerificationException e) {
			log.error("Error Verification = %s", e.getMessage());
			return false;
		}
	}
}
