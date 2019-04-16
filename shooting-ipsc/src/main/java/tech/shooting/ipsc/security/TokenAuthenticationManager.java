package tech.shooting.ipsc.security;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.TokenUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;

@Component
@Slf4j
public class TokenAuthenticationManager implements AuthenticationManager {
	@Autowired
	private TokenUtils tokenUtils;

	@Override
	public Authentication authenticate (Authentication authentication) throws AuthenticationException {
		log.debug(" Token Authentication MANAGER Start WORK");
		try {
			if(authentication instanceof TokenAuthentication) {
				TokenAuthentication readyTokenAuthentication = processAuthentication((TokenAuthentication) authentication);
				return readyTokenAuthentication;
			} else {
				authentication.setAuthenticated(false);
				return authentication;
			}
		} catch(Exception ex) {
			if(ex instanceof AuthenticationServiceException)
				try {
					throw ex;
				} catch(Exception e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	private TokenAuthentication processAuthentication (TokenAuthentication authentication) throws AuthenticationException, IOException {
		String token = authentication.getToken();
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		try {
			String grantedRole = "ROLE_UNKNOWN";
			if(token != null) {
				grantedRole = "ROLE_".concat(tokenUtils.getRoleFromToken(token).name());
			}
			authorities.add(new SimpleGrantedAuthority(grantedRole));
		} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
			log.error(" AUTH ERROR %s", e);
			return null;
		}
		User newUser = null;
		boolean isAuthenticated = false;
		if(token != null) {
			newUser = new User(tokenUtils.getLoginFromToken(token), "root", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			isAuthenticated = true;
		} else {
			newUser = new User("unknown", "empty", false, false, false, false, authorities);
		}
		TokenAuthentication fullTokenAuthentication = new TokenAuthentication(tokenUtils, authentication.getToken(), authorities, isAuthenticated, newUser);
		return fullTokenAuthentication;
	}
}