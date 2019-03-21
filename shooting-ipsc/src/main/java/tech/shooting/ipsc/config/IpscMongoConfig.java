package tech.shooting.ipsc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.shooting.commons.mongo.MongoConfig;

import java.util.List;

@Configuration
public class IpscMongoConfig extends MongoConfig {
	@Bean
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected List<Converter<?, ?>> customConversions () {
		List<Converter<?, ?>> converterList = super.customConversions();
		return converterList;
	}
	//
	//    @Bean
	//    public com.fasterxml.jackson.databind.Module registerGeoJsonModule() {
	//        return new GeoJsonModule();
	//    }
}
