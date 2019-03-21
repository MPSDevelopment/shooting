package tech.shooting.ipsc.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Component
@Slf4j
public class PasswordValidator implements ConstraintValidator<EnablePasswordConstraint, Object> {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void initialize (final EnablePasswordConstraint constraint) {
	}

	@Override
	public boolean isValid (final Object o, final ConstraintValidatorContext context) {
		boolean result = true;
		try {
			String passwordField = null;
			String idField = null;
			String loginField = null;
			String password, userId, login;
			final Class<?> clazz = o.getClass();
			final Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields) {
				if(field.isAnnotationPresent(UserIdForValidPassword.class)) {
					idField = field.getName();
				} else if(field.isAnnotationPresent(LoginForValidPassword.class)) {
					loginField = field.getName();
				} else if(field.isAnnotationPresent(ValidPassword.class)) {
					passwordField = field.getName();
				}
				if(passwordField != null && (idField != null || loginField != null)) {
					User user = null;
					password = BeanUtils.getProperty(o, passwordField).trim();
					if(idField != null) {
						userId = BeanUtils.getProperty(o, idField);
						log.info("UserId for password to check is %s", userId);
						user = userRepository.findById(Long.valueOf(userId)).orElse(null);
						if(user == null) {
							log.info("UserId with id %s does not exists", userId);
							context.disableDefaultConstraintViolation();
							context.buildConstraintViolationWithTemplate("User does not exists").addPropertyNode(idField).addConstraintViolation();
							return false;
						}
					} else {
						login = loginField == null ? null : BeanUtils.getProperty(o, loginField);
						log.info("Login for password to check is %s", login);
						user = userRepository.findByLogin(login.trim().toLowerCase());
						if(user == null) {
							log.info("UserId with login %s does not exists", login);
							context.disableDefaultConstraintViolation();
							context.buildConstraintViolationWithTemplate("User does not exists").addPropertyNode(loginField).addConstraintViolation();
							return false;
						}
					}
					if(passwordEncoder.matches(password, user.getPassword())) {
						log.info("Password is correct");
						return true;
					}
					log.info("Password is not correct");
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("Password is not correct").addPropertyNode(passwordField).addConstraintViolation();
					return false;
				}
			}
		} catch(final Exception e) {
			log.error("Cannot check old password because %s", e.getMessage());
		}
		return result;
	}
}