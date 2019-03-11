package tech.shooting.ipsc.advice;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler for catching rest server exceptions
 */
@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

	@ExceptionHandler({InvalidClaimException.class, TokenExpiredException.class, SignatureVerificationException.class})
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public @ResponseBody
	String handleRequestException (Exception ex, HttpServletResponse response) {
		log.error("User token expired!");
		Cookie cookie = new Cookie("accessToken", "");
		cookie.setPath("/");
		response.addCookie(cookie);
		return "User login expired, please reconnect!";
	}
}
