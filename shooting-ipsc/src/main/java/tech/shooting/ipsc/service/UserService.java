package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.utils.UserLockUtils;

import java.util.List;

@Slf4j
@Service
public class UserService {
//	@Autowired
//	private UserLockUtils userLockUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User checkUserInDB(String userLogin, String userPassword) {
		String login = userLogin.trim().toLowerCase();
		String password = userPassword.trim();
		User databaseUser = userRepository.findByLogin(login);
		if (databaseUser != null) {

			log.error("User with login %s has been found", userLogin);

			boolean ok = passwordEncoder.matches(password, databaseUser.getPassword());
			if (ok) {
				log.info("  PASSWORD  OK. user active %s", databaseUser.isActive());
//				userLockUtils.successfulLogin(login);
				return databaseUser;
			} else {
				log.error("  PASSWORD %s does not match %s (%s)", databaseUser.getPassword(), password, passwordEncoder.encode(password));
//				userLockUtils.unsuccessfulLogin(login);
			}
		}
		log.info("  PASSWORD  FAIL");
		return null;
	}

	private User createUser(User user, RoleName role) {
		log.info("User with login %s", user.getLogin());
		if (userRepository.findByLogin(user.getLogin()) != null) {
			throw new ValidationException(User.LOGIN_FIELD, "User with login %s already exists", user.getLogin());
		}
		user.setRoleName(role);
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public User add(UserSignupBean signupUser, RoleName role) {
		User user = new User();
		BeanUtils.copyProperties(signupUser, user);
		return createUser(user, role);
	}

	public User updateUser(Long userId, UserUpdateBean bean) throws BadRequestException {
		checkPathIdAndCurrentId(userId, bean.getId());
		User dbUser = getDbUserIfExist(bean.getId());
		BeanUtils.copyProperties(bean, dbUser);
		return userRepository.save(dbUser);
	}

	public User getDbUserIfExist(long id) throws BadRequestException {
		return userRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", id)));
	}

	public User changePassword(Long userId, ChangePasswordBean bean) throws BadRequestException {
		checkPathIdAndCurrentId(userId, bean.getId());
		User dbUser = getDbUserIfExist(bean.getId());
		dbUser.setPassword(passwordEncoder.encode(bean.getNewPassword().trim()));
		dbUser = userRepository.save(dbUser);
		log.info("Password has been changed for the user %s", dbUser.getLogin());
		return dbUser;
	}

	private void checkPathIdAndCurrentId(Long userId, Long beanId) throws BadRequestException {
		if (!userId.equals(beanId)) {
			throw new BadRequestException(new ErrorMessage("Path userId %s does not match bean userId %s", userId, beanId));
		}
	}

	public void deleteUser(Long userId) throws BadRequestException {
		log.info("Trying to delete user by id %s", userId);
		User user = getDbUserIfExist(userId);
		userRepository.delete(user);
	}

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public Long getCount() {
		return userRepository.count();
	}

	public ResponseEntity<List<User>> getUsersByPage(Integer page, Integer size) {
		return Pageable.getPage(page, size, userRepository);
	}

	public List<User> getListJudges() {
		return userRepository.findByRoleName(RoleName.JUDGE);
	}

	public List<User> getListUsers() {
		return userRepository.findByRoleName(RoleName.USER);
	}
}
