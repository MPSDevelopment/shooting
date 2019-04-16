package tech.shooting.ipsc.security;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.TokenUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Slf4j
@ToString
public class TokenAuthentication implements Authentication {
	private static final long serialVersionUID = -8128975069190073254L;

	private String token;

	private Collection<? extends GrantedAuthority> authorities;

	private boolean isAuthenticated;

	private UserDetails principal;

	private Object details;

	private TokenUtils tokenUtils;

	public TokenAuthentication (TokenUtils tokenUtils, String token) {
		this.tokenUtils = tokenUtils;
		this.token = token;
		// this.details = details;
	}

	public TokenAuthentication (TokenUtils tokenUtils, String token, Collection<GrantedAuthority> authorities, boolean isAuthenticated, UserDetails principal) {
		this.tokenUtils = tokenUtils;
		this.token = token;
		this.authorities = authorities;
		this.isAuthenticated = isAuthenticated;
		this.principal = principal;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities () {
		return authorities;
	}

	@Override
	public Object getCredentials () {
		return null;
	}

	@Override
	public Object getDetails () {
		return details;
	}

	@Override
	public String getName () {
		if(principal != null)
			return principal.getUsername();
		else
			return null;
	}

	@Override
	public Object getPrincipal () {
		return principal;
	}

	@Override
	public boolean isAuthenticated () {
		return isAuthenticated;
	}

	@Override
	public void setAuthenticated (boolean b) throws IllegalArgumentException {
		isAuthenticated = b;
	}

	public String getToken () {
		return token;
	}

	public String getUserLogin () {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		try {
			return tokenUtils.getLoginFromToken(token);
		} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
			log.error("Cannot get a user id from token %s", token);
			return null;
		}
	}

	public String getUserId () {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		try {
			return tokenUtils.getIdFromToken(token).toString();
		} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
			log.error("Cannot get a user id from token %s", token);
			return null;
		}
	}

	public String getRole () {
		if(StringUtils.isBlank(token)) {
			return null;
		}
		try {
			return tokenUtils.getRoleFromToken(token).name();
		} catch(InvalidClaimException | TokenExpiredException | SignatureVerificationException e) {
			log.error("Cannot get a user id from token %s", token);
			return null;
		}
	}
}
