package tech.shooting.ipsc.config;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tech.shooting.commons.constraints.Version;

@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig extends WebMvcConfigurationSupport {
	
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

	@Bean
	public Docket apiVersion10(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Version 1.0").pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.regex(Version.INCLUDE_VERSION_REGEXP)).build();
	}
	
	@Bean
	public Docket apiOld(ServletContext servletContext) {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Version old").pathMapping(SLASH_API).apiInfo(new ApiInfoBuilder().title("IPSC Service REST API").description("All the methods of the REST API").build()).select()
				.apis(RequestHandlerSelectors.basePackage("tech.shooting.ipsc")).paths(PathSelectors.regex(Version.NOT_INCLUDE_VERSION_REGEXP)).build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/static/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/static/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/static/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

	}
}
