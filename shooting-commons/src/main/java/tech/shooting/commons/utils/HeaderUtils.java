package tech.shooting.commons.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.Token;

@Component
@Slf4j
public class HeaderUtils {

	public final static String USER_ID_HEADER = "UserId";

	public final static String USER_LOGIN_HEADER = "UserLogin";

	public final static String NAME_HEADER = "UserEmail";

	public final static String ROLE_HEADER = "UserRole";

	public final static String TOKEN_HEADER = "Token";
	
	public final static String X_FORWARDED_FOR = "X-Forwarded-For";
	
	public final static String PAGE_HEADER = "page";
	
	public final static String PAGES_HEADER = "pages";
	
	public final static String TOTAL_HEADER = "total";

	public static String getAuthToken(HttpServletRequest request) {
		return request.getHeader(Token.TOKEN_HEADER);
	}

	public static void setAuthToken(HttpServletResponse response, String token) {
		response.setHeader(Token.TOKEN_HEADER, token);
	}

	public static void checkUserId(String userId) throws BadRequestException {
		if (StringUtils.isBlank(userId)) {
			log.error("UserId is empty");
			throw new BadRequestException(new ErrorMessage("userId is empty"));
		}
		if (!StringUtils.isNumeric(userId)) {
			log.error("UserId %s is not numeric %s", userId);
			throw new BadRequestException(new ErrorMessage("userId is empty"));
		}
	}

}
