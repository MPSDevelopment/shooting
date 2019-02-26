package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.USER_CONTROLLER)
@Api(value = ControllerAPI.USER_CONTROLLER)
@Slf4j
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_CREATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new User", notes = "Creates new User")
	public ResponseEntity<User> signupUser(HttpServletRequest request, @RequestBody @Valid UserSignupBean signupUser) throws BadRequestException {
		User user = new User();
		BeanUtils.copyProperties(signupUser, user);
		signupUser(request, user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	private void signupUser(HttpServletRequest request, User user) {
		log.info("Signing up user with login %s", user.getLogin());

		if (userRepository.findByLogin(user.getLogin()) != null) {
			throw new ValidationException(User.LOGIN_FIELD, "User with login %s already exists", user.getLogin());
		}

		user.setRoleName(RoleName.USER);
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);

	}

}
