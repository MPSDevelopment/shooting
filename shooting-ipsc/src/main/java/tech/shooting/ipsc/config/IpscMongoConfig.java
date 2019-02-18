package tech.shooting.ipsc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import tech.shooting.commons.mongo.MongoConfig;

import java.util.List;

@Configuration
public class IpscMongoConfig extends MongoConfig {

    @Override
    protected List<Converter<?, ?>> customConversions() {
        List<Converter<?, ?>> converterList = super.customConversions();
        return converterList;
    }

//
//    @Bean
//    public com.fasterxml.jackson.databind.Module registerGeoJsonModule() {
//        return new GeoJsonModule();
//    }


}
