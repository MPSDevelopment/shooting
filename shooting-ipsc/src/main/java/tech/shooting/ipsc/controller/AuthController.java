package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotModifiedException;
import tech.shooting.commons.exception.RequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.utils.HeaderUtils;
import tech.shooting.ipsc.bean.TokenLogin;
import tech.shooting.ipsc.bean.UserLogin;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.UserService;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(ControllerAPI.AUTH_CONTROLLER)
@Api(value = ControllerAPI.AUTH_CONTROLLER)
@Slf4j
public class AuthController {

	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGIN, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "User Login")
	public ResponseEntity<TokenLogin> login(HttpServletResponse response, @RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @RequestBody @Valid UserLogin user) throws RequestException {
		log.info("Start Login User with email = %s", user.getEmail());
		if (StringUtils.isNotBlank(token) && tokenUtils.verifyToken(token)) {
			throw new NotModifiedException(new ErrorMessage("User has been already logged in"));
		}

		user.setEmail(user.getEmail().trim().toLowerCase());
		user.setPassword(user.getPassword().trim());

		User databaseUser = Optional.ofNullable(userService.checkUserInDB(user.getEmail(), user.getPassword())).orElseThrow(() -> new ValidationException(User.EMAIL_FIELD, "User with login %s does not exist", user.getEmail()));

		if (BooleanUtils.isNotTrue(databaseUser.isActive())) {
			log.info("  USER DIDN'T CONFIRM REGISTRATION");
			throw new BadRequestException(new ErrorMessage("User didn't confirm registration"));
		}

		RoleName usersRole = databaseUser.getRoleName();

		token = tokenUtils.createToken(databaseUser.getId(), Token.TokenType.USER, "", user.getEmail(), usersRole, DateUtils.addMonths(new Date(), 1), DateUtils.addDays(new Date(), -1));
		log.info("User %s has been logged in with role = %s", user.getEmail(), usersRole);

		HeaderUtils.setAuthToken(response, token);

		return new ResponseEntity<>(new TokenLogin(token), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.AUTH_CONTROLLER_POST_LOGOUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "User Logout")
	public ResponseEntity<SuccessfulMessage> logout(HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
		Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).orElseThrow(() -> new BadRequestException(new ErrorMessage("User was not logged in")));
		new SecurityContextLogoutHandler().logout(request, response, auth);
		log.info("User %s has been logged out", auth.getName());
		return new ResponseEntity<>(new SuccessfulMessage("User has been logged out"), HttpStatus.OK);
	}

}
