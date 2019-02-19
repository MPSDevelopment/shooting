package tech.shooting.ipsc.service;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.utils.UserLockUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public User getByToken(String value) {
        if (value != null && !value.isEmpty()) {
            String login;
            try {
                login = tokenUtils.getLoginFromToken(value);
                if (login != null && !login.isEmpty()) {
                    return userRepository.findByLogin(login);
                }
            } catch (InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
                log.error("Cannot get a user by token %s because %s", value, e.getMessage());
            }
        }
        return null;
    }

    public User checkUserInDB(String userLogin, String userPassword) {
        String login = userLogin.trim().toLowerCase();
        String password = userPassword.trim();

        User databaseUser = userRepository.findByLogin(login);
        if (databaseUser != null) {
            boolean ok = passwordEncoder.matches(password, databaseUser.getPassword());
            if (ok) {
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


}
