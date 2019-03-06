package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.service.UserService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
	@ApiOperation(value = "Add new judge", notes = "Creates new Judge")
	public ResponseEntity<User> signupJudge(HttpServletRequest request, @RequestBody @Valid UserSignupBean signupUser) throws BadRequestException {
		User user = new User();
		BeanUtils.copyProperties(signupUser, user);
		signupJudge(request, user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	private void signupJudge(HttpServletRequest request, User user) {
		log.info("Signing up judge with login %s", user.getLogin());

		if (userRepository.findByLogin(user.getLogin()) != null) {
			throw new ValidationException(User.LOGIN_FIELD, "User with login %s already exists", user.getLogin());
		}

		user.setRoleName(RoleName.JUDGE);
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);

	}

//	@PreAuthorize(IpscConstants.ADMIN_ROLE)
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_PUT_UPDATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Edit existing Judge", notes = "Update existing Judge")
	public ResponseEntity<User> updateUser(@PathVariable(value = "userId", required = true) Long userId, @RequestBody @Valid UserUpdateBean bean) throws BadRequestException {
		
		if (!userId.equals(bean.getId())) {
			throw new BadRequestException(new ErrorMessage("Path userId %s does not match bean userId %s", userId, bean.getId()));
		}
		
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));

		dbUser.setName(bean.getName());
		dbUser.setAddress(bean.getAddress());
		dbUser.setActive(bean.isActive());
		dbUser.setLogin(bean.getLogin());
		dbUser.setBirthDate(bean.getBirthDate());
		
		userRepository.save(dbUser);

		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}

//	@PreAuthorize(IpscConstants.ADMIN_ROLE)
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update judge password", notes = "Update judge password")
	public ResponseEntity<User> updatePassword(@RequestBody @Valid ChangePasswordBean bean) throws BadRequestException {
		
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));
		dbUser.setPassword(passwordEncoder.encode(bean.getNewPassword().trim()));
		userRepository.save(dbUser);

		log.info("Password has been changed for the user %s", dbUser.getLogin());
		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}
	
    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_DELETE_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Delete User", notes = "Returns deleted user object")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "userId", required = true) Long userId) throws BadRequestException {
        log.info("Trying to delete user by id %s", userId);
        
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", userId)));
        userRepository.delete(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_USER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get User", notes = "Returns user object")
    public ResponseEntity<User> getUser(@PathVariable(value = "userId", required = true) Long userId) throws BadRequestException {
      
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", userId)));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_ALL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get all users", notes = "Returns all user objects")
    public ResponseEntity<List<User>> getUsers() throws BadRequestException {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }
    
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.USER_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get all users count", notes = "Returns all users count")
    public ResponseEntity<Long> getCount() throws BadRequestException {
        return new ResponseEntity<>(userRepository.count(), HttpStatus.OK);
    }

}
