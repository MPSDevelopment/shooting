package tech.shooting.ipsc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tech.shooting.commons.utils.JacksonUtils;

import javax.servlet.ServletContext;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableSwagger2
@Slf4j
public class AppConfig extends WebMvcConfigurationSupport {

	public static final String NOT_INCLUDE_VERSION_REGEXP = "^((?!v[0123456789.]*).)*$";

	public static final String INCLUDE_VERSION_REGEXP = ".*/v[0123456789.]*/?.*";

	private static final String SLASH_API = "";

	@Autowired
	private IpscSettings settings;

//	@Bean
//	public HandlerInstantiator handlerInstantiator(ApplicationContext applicationContext) {
//	    return new SpringHandlerInstantiator(applicationContext.getAutowireCapableBeanFactory());
//	}

	@Bean
	public UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl", // url
				"none", // docExpansion => none | list
				"alpha", // apiSorter => alpha
				"model", // defaultModelRendering => schema
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, true, // enableJsonEditor => true | false
				true, // showRequestHeaders => true | false
				60000L); // requestTimeout => in milliseconds, defaults to null (uses jquery xh timeout)
	}

	@Bean
	public Docket apiVersion10(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Version 1.0").pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.regex(INCLUDE_VERSION_REGEXP)).build();
	}

	@Bean
	public Docket api(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.any()).build();
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/**");
		resolver.setSuffix(".html");
		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/static/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/static/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/static/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//		registry.addResourceHandler("/**").addResourceLocations("/static/");

//		registry.addResourceHandler("/**.js", "/**.map").addResourceLocations("classpath:/static/");
//		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");

		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").resourceChain(true).addResolver(new PathResourceResolver() {
			@Override
			protected Resource getResource(String resourcePath, Resource location) throws IOException {
				Resource requestedResource = location.createRelative(resourcePath);
				return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : new ClassPathResource("/static/index.html");
			}
		});
	}

	@Bean(name = "mappingJackson2HttpMessageConverter")
	public MappingJackson2HttpMessageConverter converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = JacksonUtils.getMapper();
		converter.setObjectMapper(mapper);
		return converter;
	}
	
	@Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter(){
        ByteArrayHttpMessageConverter bam = new ByteArrayHttpMessageConverter();
        List<org.springframework.http.MediaType> mediaTypes = new LinkedList<MediaType>();
        mediaTypes.add(org.springframework.http.MediaType.APPLICATION_JSON);
        mediaTypes.add(org.springframework.http.MediaType.IMAGE_JPEG);
        mediaTypes.add(org.springframework.http.MediaType.IMAGE_PNG);
        mediaTypes.add(org.springframework.http.MediaType.IMAGE_GIF);
        mediaTypes.add(org.springframework.http.MediaType.TEXT_PLAIN);
        bam.setSupportedMediaTypes(mediaTypes);
        return bam;
    }

//	@Bean
//	public Jackson2ObjectMapperBuilder objectMapperBuilder(HandlerInstantiator handlerInstantiator) {
//	    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//	    builder.handlerInstantiator(handlerInstantiator);
//	    return builder;
//	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(converter());
		converters.add(new ResourceHttpMessageConverter());
		converters.add(byteArrayHttpMessageConverter());
		super.configureMessageConverters(converters);
	}

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setObjectMapper(JacksonUtils.getMapper());
		return jackson2HttpMessageConverter;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = corsSource();
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(Collections.singletonList("*"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		config.setExposedHeaders(Collections.singletonList("page,pages,total"));
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
