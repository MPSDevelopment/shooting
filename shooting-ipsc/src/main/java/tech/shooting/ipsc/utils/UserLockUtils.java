package tech.shooting.ipsc.utils;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserLockUtils {

	private final static int MAX_ATTEMPT_NUMBER = 7;
	private Map<String, Integer> unsuccessfulAttemptLoginMap = new HashMap<>();

	@Autowired
	private UserRepository userRepository;

	public void successfulLogin(String login) {
		Optional.ofNullable(login).orElseThrow(() -> new IllegalArgumentException("Login must not be null"));
		unsuccessfulAttemptLoginMap.remove(login);
	}

	// Lock user after 7 unsuccessful login attempts
	public void unsuccessfulLogin(String login) {
		Optional.ofNullable(login).orElseThrow(() -> new IllegalArgumentException("Login must not be null"));
		Integer attemptNumber = unsuccessfulAttemptLoginMap.get(login);

		if (attemptNumber == null) {
			attemptNumber = 1;
			unsuccessfulAttemptLoginMap.put(login, attemptNumber);
		} else {
			switch (attemptNumber) {
			case MAX_ATTEMPT_NUMBER - 1:
				userLock(login);
				break;
			default:
				attemptNumber++;
				unsuccessfulAttemptLoginMap.put(login, attemptNumber);
			}
		}
	}

	private void userLock(String login) {
		User user = Optional.ofNullable(userRepository.findByLoginAndActive(login, true)).orElseThrow(() -> new ValidationException(User.LOGIN_FIELD, "There is no such active user with login %s", login));
		user.setActive(false);
		userRepository.save(user);
	}
}
