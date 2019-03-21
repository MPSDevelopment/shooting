package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.USER_CONTROLLER)
@Api(value = ControllerAPI.USER_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_POST_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new judge", notes = "Creates new Judge")
	public ResponseEntity<User> signupJudge (@RequestBody @Valid UserSignupBean signupUser) {
		return new ResponseEntity<>(userService.addJudge(signupUser), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_PUT_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Edit existing Judge", notes = "Update existing Judge")
	public ResponseEntity<User> updateJudge (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId, @RequestBody @Valid UserUpdateBean bean) throws BadRequestException {
		return new ResponseEntity<>(userService.updateJudge(userId, bean), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update user password", notes = "Update user password")
	public ResponseEntity<User> updatePassword (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId, @RequestBody @Valid ChangePasswordBean bean) throws BadRequestException {
		return new ResponseEntity<>(userService.changePassword(userId, bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete User", notes = "Returns deleted user object")
	public ResponseEntity<Void> deleteUser (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId) throws BadRequestException {
		userService.deleteUser(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get User", notes = "Returns user object")
	public ResponseEntity<User> getUser (@PathVariable(value = ControllerAPI.PATH_VARIABLE_USER_ID) Long userId) throws BadRequestException {
		return new ResponseEntity<>(userService.getDbUserIfExist(userId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all users", notes = "Returns all user objects")
	public ResponseEntity<List<User>> getUsers () {
		return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all users count", notes = "Returns all users count")
	public ResponseEntity<Long> getCount () {
		return new ResponseEntity<>(userService.getCount(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USERS_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get users by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class), @ResponseHeader(name = "total", description = "Total " +
		"records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<User>> getUsers (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return userService.getUsersByPage(page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_JUDGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list judges", notes = "Return list of judges object")
	public ResponseEntity<List<User>> getJudges () {
		return new ResponseEntity<>(userService.getListJudges(), HttpStatus.OK);
	}

}
