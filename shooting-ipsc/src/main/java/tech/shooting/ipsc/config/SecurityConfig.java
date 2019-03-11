package tech.shooting.ipsc.config;

import org.springframework.beans.factory.annotation.Autowired;
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
import tech.shooting.ipsc.controller.ControllerAPI;
import tech.shooting.ipsc.security.RestAccessDeniedHandler;
import tech.shooting.ipsc.security.RestAuthenticationEntryPoint;
import tech.shooting.ipsc.security.TokenAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
        // -- swagger ui
        "/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/error"
        // other public endpoints of your API may be appended to this array
    };

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    public void configure (WebSecurity web) {
        web.ignoring().antMatchers(AUTH_WHITELIST).antMatchers("/api/auth" + ControllerAPI.VERSION_1_0 + "/login**").antMatchers("/favicon.ico").antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint()).accessDeniedHandler(new RestAccessDeniedHandler());
        http.userDetailsService(userDetailsService);
        http.addFilterBefore(tokenAuthenticationFilter, TokenAuthenticationFilter.class);
        http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/auth" + ControllerAPI.VERSION_1_0 + "/login**").permitAll().antMatchers("/api/auth" + ControllerAPI.VERSION_1_0 + "/logout").permitAll().and().authorizeRequests().anyRequest().hasRole("ADMIN");
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