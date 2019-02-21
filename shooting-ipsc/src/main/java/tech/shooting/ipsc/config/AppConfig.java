package tech.shooting.ipsc.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tech.shooting.commons.utils.JacksonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger2
@Slf4j
public class AppConfig extends WebMvcConfigurationSupport {
	
	@Autowired
	private IpscSettings settings;

	public AppConfig() {
		super();
	}
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "index.html");
        registry.addRedirectViewController("/doc", "swagger-ui.html");
    }

//	@Bean(name = "mappingJackson2HttpMessageConverter")
	private MappingJackson2HttpMessageConverter converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = JacksonUtils.getMapper();
		converter.setObjectMapper(mapper);
		return converter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
//		super.configureMessageConverters(converters);
	}

	private HttpMessageConverter<Object> mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setObjectMapper(JacksonUtils.getMapper());
		return jackson2HttpMessageConverter;
	}
	
	private static final String SLASH_API = "";

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

//	@Bean
//	public Docket apiVersion10(ServletContext servletContext) {
//		return new Docket(DocumentationType.SWAGGER_2).groupName("Version 1.0").pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
//				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.regex(Version.INCLUDE_VERSION_REGEXP)).build();
//	}
	
	@Bean
	public Docket api(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.any()).build();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		
		registry.addResourceHandler("/static/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/static/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/static/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		
//		registry.addResourceHandler("index.html").addResourceLocations("file:" + settings.getFrontendFolder() + "/index.html");
//		registry.addResourceHandler("^/((?!api|doc|swagger|webjars|image|error).)*$").addResourceLocations("file:" + settings.getFrontendFolder());

	}
}
