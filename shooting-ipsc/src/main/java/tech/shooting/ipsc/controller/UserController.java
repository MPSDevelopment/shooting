package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.UserService;

import javax.validation.Valid;
import java.util.List;

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

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new judge", notes = "Creates new Judge")
	public ResponseEntity<User> signupJudge (@RequestBody @Valid UserSignupBean signupUser) throws BadRequestException {
		return new ResponseEntity<>(userService.addJudge(signupUser), HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_PUT_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Edit existing Judge", notes = "Update existing Judge")
	public ResponseEntity<User> updateJudge (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId, @RequestBody @Valid UserUpdateBean bean) throws BadRequestException {
		return new ResponseEntity<>(userService.updateJudge(userId, bean), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update user password", notes = "Update user password")
	public ResponseEntity<User> updatePassword (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId, @RequestBody @Valid ChangePasswordBean bean) throws BadRequestException {
		return new ResponseEntity<>(userService.changePassword(userId, bean), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete User", notes = "Returns deleted user object")
	public ResponseEntity deleteUser (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId) throws BadRequestException {
		userService.deleteUser(userId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get User", notes = "Returns user object")
	public ResponseEntity<User> getUser (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID, required = true) Long userId) throws BadRequestException {

		User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", userId)));
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all users", notes = "Returns all user objects")
	public ResponseEntity<List<User>> getUsers () throws BadRequestException {
		return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all users count", notes = "Returns all users count")
	public ResponseEntity<Long> getCount () throws BadRequestException {
		return new ResponseEntity<>(userRepository.count(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get users by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class), @ResponseHeader(name = "total", description = "Total " +
		"records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<User>> getUsers (@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page,
	                                            @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) throws BadRequestException {
		return PageAble.getPage(page, size, userRepository);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_JUDGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list judges", notes = "Return list of judges object")
	public ResponseEntity<List<User>> getJudges () {
		return new ResponseEntity<>(userRepository.findByRoleName(RoleName.JUDGE), HttpStatus.OK);
	}

}
