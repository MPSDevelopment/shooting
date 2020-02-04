package tech.shooting.ipsc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.controller.ControllerAPI;
import tech.shooting.ipsc.security.IpscUserDetailsService;
import tech.shooting.ipsc.security.RestAccessDeniedHandler;
import tech.shooting.ipsc.security.RestAuthenticationEntryPoint;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;
import tech.shooting.ipsc.security.TokenAuthenticationManager;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String[] AUTH_WHITELIST = {
		// -- swagger ui
		"/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/error", "/index.html", "/favicon.ico", "/*.js"
		// other public endpoints of your API may be appended to this array
	};

	@Autowired
	@Qualifier("ipscUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private TokenAuthenticationFilter tokenAuthenticationFilter;
	
	@Bean
	@ConditionalOnMissingBean
	public TokenUtils tokenUtils() {
		return new TokenUtils();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public IpscUserDetailsService ipscUserDetailsService() {
		return new IpscUserDetailsService();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TokenAuthenticationManager tokenAuthenticationManager() {
		return new TokenAuthenticationManager();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter();
	}

	@Override
	public void configure (WebSecurity web) {
		web.ignoring().antMatchers(AUTH_WHITELIST)
			.antMatchers(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + "/login**")
			.antMatchers(ControllerAPI.VALIDATION_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
			.antMatchers(HttpMethod.GET, ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
			.antMatchers(HttpMethod.GET, ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
			.antMatchers(HttpMethod.GET, ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
			.antMatchers(HttpMethod.POST, ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
			.antMatchers("/favicon.ico")
			.antMatchers("/**.html")
			.antMatchers("/**.js")
			.antMatchers("/**.png")
			.antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Override
	protected void configure (HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint()).accessDeniedHandler(new RestAccessDeniedHandler());
		http.userDetailsService(userDetailsService);
		http.addFilterBefore(tokenAuthenticationFilter, TokenAuthenticationFilter.class);
		http.antMatcher("/api/**")
		    .authorizeRequests()
		    .antMatchers(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + "/login**")
		    .permitAll()
		    .antMatchers(ControllerAPI.AUTH_CONTROLLER + ControllerAPI.VERSION_1_0 + "/logout")
		    .permitAll()
		    .antMatchers(ControllerAPI.VALIDATION_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
		    .permitAll()
		    .antMatchers(HttpMethod.GET, ControllerAPI.IMAGE_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
		    .permitAll()
		    .antMatchers(HttpMethod.GET, ControllerAPI.MAP_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
		    .permitAll()
		    .antMatchers(HttpMethod.GET, ControllerAPI.TAG_CONTROLLER + ControllerAPI.VERSION_1_0 + "/**")
		    .permitAll()
		    .and()
		    .authorizeRequests()
		    .anyRequest().hasAnyRole("ADMIN", "JUDGE", "USER", "GUEST");
		
//		http.antMatcher("/**").authorizeRequests().anyRequest().anonymous();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource () {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public RequestContextListener requestContextListener () {
		return new RequestContextListener();
	}
}