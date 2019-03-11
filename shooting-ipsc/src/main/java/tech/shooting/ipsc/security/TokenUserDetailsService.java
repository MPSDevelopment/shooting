package tech.shooting.ipsc.security;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.shooting.commons.utils.HeaderUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Service
@NoArgsConstructor
public class TokenUserDetailsService implements UserDetailsService {

	@Autowired(required = true)
	private TokenUtils tokenUtils;

	public static List<GrantedAuthority> getGrantedAuthorities (String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		SimpleGrantedAuthority simpleAuth = new SimpleGrantedAuthority("ROLE_".concat(role));
		log.info("  Simple  AUTH = %s ", simpleAuth);
		grantedAuthorities.add(simpleAuth);
		return grantedAuthorities;
	}

	@Override
	public UserDetails loadUserByUsername (String login) throws UsernameNotFoundException {
		log.info("TEST REQUEST - %s", login);

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userName = request.getHeader(HeaderUtils.NAME_HEADER);
		String roleName = request.getHeader(HeaderUtils.ROLE_HEADER);

		if(userName == null) {
			throw new UsernameNotFoundException("User not found");
		}

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		log.info(" USER ROLE- %s ", getGrantedAuthorities(roleName));
		User newUser = new User(userName, null, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, getGrantedAuthorities(roleName));
		log.info(" USER FROM SERVICE - %s   autority = %s", newUser, newUser.getAuthorities());
		return newUser;
	}
}
