package tech.shooting.ipsc.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.utils.JacksonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@EnableWebMvc
@Configuration
@Slf4j
public class AppConfig implements WebMvcConfigurer {
	
	@Autowired
	private IpscSettings settings;

	public AppConfig() {
		super();
	}
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "index.html");
    }

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/api/**");
		registry.addResourceHandler("index.html").addResourceLocations("file:" + settings.getFrontendFolder() + "/index.html");
		registry.addResourceHandler("^(?!/(api|doc|swagger|webjars|image|error).*)$").addResourceLocations("file:" + settings.getFrontendFolder());
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
}
