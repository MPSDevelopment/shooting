package tech.shooting.ipsc.service;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.ChangePasswordBean;
import tech.shooting.ipsc.bean.UserSignupBean;
import tech.shooting.ipsc.bean.UserUpdateBean;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.utils.UserLockUtils;

@Slf4j
@Service
public class UserService {
	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserLockUtils userLockUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getByToken (String value) {
		if(value != null && !value.isEmpty()) {
			String login;
			try {
				login = tokenUtils.getLoginFromToken(value);
				if(login != null && !login.isEmpty()) {
					return userRepository.findByLogin(login);
				}
			} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
				log.error("Cannot get a user by token %s because %s", value, e.getMessage());
			}
		}
		return null;
	}

	public User checkUserInDB (String userLogin, String userPassword) {
		String login = userLogin.trim().toLowerCase();
		String password = userPassword.trim();
		User databaseUser = userRepository.findByLogin(login);
		if(databaseUser != null) {
			boolean ok = passwordEncoder.matches(password, databaseUser.getPassword());
			if(ok) {
				log.info("  PASSWORD  OK. user active %s", databaseUser.isActive());
				userLockUtils.successfulLogin(login);
				return databaseUser;
			} else {
				log.error("  PASSWORD  does not match");
				userLockUtils.unsuccessfulLogin(login);
			}
		}
		log.info("  PASSWORD  FAIL");
		return null;
	}

	private void signupJudge (User user) {
		log.info("Signing up judge with login %s", user.getLogin());
		if(userRepository.findByLogin(user.getLogin()) != null) {
			throw new ValidationException(User.LOGIN_FIELD, "User with login %s already exists", user.getLogin());
		}
		user.setRoleName(RoleName.JUDGE);
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	public User addJudge (UserSignupBean signupUser) {
		User user = new User();
		BeanUtils.copyProperties(signupUser, user);
		signupJudge(user);
		return user;
	}

	public User updateJudge (Long userId, UserUpdateBean bean) throws BadRequestException {
		checkPathIdAndCurrentId(userId, bean.getId());
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));
		BeanUtils.copyProperties(bean, dbUser);
		userRepository.save(dbUser);
		return dbUser;
	}

	public User changePassword (Long userId, ChangePasswordBean bean) throws BadRequestException {
		checkPathIdAndCurrentId(userId, bean.getId());
		User dbUser = userRepository.findById(bean.getId()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", bean.getId())));
		dbUser.setPassword(passwordEncoder.encode(bean.getNewPassword().trim()));
		dbUser = userRepository.save(dbUser);
		log.info("Password has been changed for the user %s", dbUser.getLogin());
		return dbUser;
	}

	private void checkPathIdAndCurrentId (Long userId, Long beanId) throws BadRequestException {
		if(!userId.equals(beanId)) {
			throw new BadRequestException(new ErrorMessage("Path userId %s does not match bean userId %s", userId, beanId));
		}
	}

	public void deleteUser (Long userId) throws BadRequestException {
		log.info("Trying to delete user by id %s", userId);
		User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect userId %s", userId)));
		userRepository.delete(user);
	}
}
