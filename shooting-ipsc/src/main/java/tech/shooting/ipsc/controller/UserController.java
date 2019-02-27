package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.config.IpscConstants;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.UserService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

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
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@PreAuthorize(IpscConstants.ADMIN_ROLE)
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

//	@PreAuthorize(IpscConstants.ADMIN_ROLE)
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_PUT_UPDATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Edit existing User", notes = "Update existing User")
	public ResponseEntity<User> updateUser(@RequestBody @Valid UserUpdateBean bean) throws BadRequestException {
		
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));

		dbUser.setName(bean.getName());
		
		userRepository.save(dbUser);

		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}

//	@PreAuthorize(IpscConstants.ADMIN_ROLE)
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update password", notes = "Update user password")
	public ResponseEntity<User> updatePassword(@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @RequestBody @Valid ChangePasswordBean bean) throws BadRequestException {
		
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));
		dbUser.setPassword(passwordEncoder.encode(bean.getNewPassword().trim()));
		userRepository.save(dbUser);

		log.info("Password has been changed for the user %s", dbUser.getLogin());
		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}

}
