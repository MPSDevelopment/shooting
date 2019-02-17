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

	public void successfulLogin(String email) {
		Optional.ofNullable(email).orElseThrow(() -> new IllegalArgumentException("Email must not be null"));
		unsuccessfulAttemptLoginMap.remove(email);
	}

	// Lock user after 7 unsuccessful login attempts
	public void unsuccessfulLogin(String email) {
		Optional.ofNullable(email).orElseThrow(() -> new IllegalArgumentException("Email must not be null"));
		Integer attemptNumber = unsuccessfulAttemptLoginMap.get(email);

		if (attemptNumber == null) {
			attemptNumber = 1;
			unsuccessfulAttemptLoginMap.put(email, attemptNumber);
		} else {
			switch (attemptNumber) {
			case MAX_ATTEMPT_NUMBER - 1:
				userLock(email);
				break;
			default:
				attemptNumber++;
				unsuccessfulAttemptLoginMap.put(email, attemptNumber);
			}
		}
	}

	private void userLock(String email) {
		User user = Optional.ofNullable(userRepository.findByEmailAndIsActive(email, true)).orElseThrow(() -> new ValidationException(User.EMAIL_FIELD, "There is no such active user with email %s", email));
		user.setActive(false);
		userRepository.save(user);
	}
}
