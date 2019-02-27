//package tech.shooting.ipsc.config;
//
//import java.util.Collections;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
////import org.springframework.web.reactive.config.CorsRegistry;
////import org.springframework.web.reactive.config.WebFluxConfigurer;
////import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Configuration
//@Slf4j
//@EnableWebMvc
//public class CorsConfig extends WebMvcConfigurationSupport {
//
//	@Bean
//	public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
//		UrlBasedCorsConfigurationSource source = corsSource();
//		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//		return bean;
//	}
//
//	@Bean
//	public UrlBasedCorsConfigurationSource corsSource() {
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		CorsConfiguration config = new CorsConfiguration();
//		config.setAllowCredentials(true);
//		config.setAllowedOrigins(Collections.singletonList("*"));
//		config.setAllowedMethods(Collections.singletonList("*"));
//		config.setAllowedHeaders(Collections.singletonList("*"));
//		source.registerCorsConfiguration("/**", config);
//		return source;
//	}
//}