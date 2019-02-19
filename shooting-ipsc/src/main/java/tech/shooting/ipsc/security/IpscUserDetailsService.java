package tech.shooting.ipsc.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.repository.UserRepository;

@Slf4j
@Service
@NoArgsConstructor
public class IpscUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		log.info("TEST REQUEST - %s", login);
		tech.shooting.ipsc.pojo.User domainUser = userRepository.findByLogin(login);

		if (domainUser == null) {
			throw new UsernameNotFoundException("User not found");
		}

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		log.info(" USER ROLE- %s ", getAuthorities(domainUser.getRoleName().name()));
		User newUser = new User(domainUser.getLogin(), domainUser.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, getAuthorities(domainUser.getRoleName().name()));
		log.info(" USER FROM SERVICE - %s   autority = %s", newUser, newUser.getAuthorities());
		return newUser;
	}

	public Collection<GrantedAuthority> getAuthorities(String role) {
		List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(role);
		return grantedAuthorities;
	}

	public static List<GrantedAuthority> getGrantedAuthorities(String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		SimpleGrantedAuthority simpleAuth = new SimpleGrantedAuthority("ROLE_" + role);
		log.info("  Simple  AUTH = %s ", simpleAuth);
		grantedAuthorities.add(simpleAuth);
		return grantedAuthorities;
	}
}
