package tech.shooting.commons.mongo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

import com.mongodb.DBRef;
import com.mongodb.MongoClientOptions;
import com.mpsdevelopment.plasticine.commons.IdGenerator;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.mongo.converter.DateToOffsetDateTimeConverter;
import tech.shooting.commons.mongo.converter.OffsetDateTimeToDateConverter;

@Slf4j
public class MongoConfig {

	@Autowired
	private MongoDbFactory mongoFactory;

	@Autowired
	private MongoMappingContext mongoMappingContext;

	@Bean
	public MongoClientOptions mongoOptions() {
		log.info("Creating mongo options");
		return MongoClientOptions.builder().socketTimeout(60 * 000).connectTimeout(30000).connectionsPerHost(200).build();
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		log.info("Creating mongo template");
		MongoTemplate mongoTemplate = new MongoTemplate(mongoFactory, mongoConverter());
		return mongoTemplate;
	}

	@Bean
	public MappingMongoConverter mongoConverter() throws Exception {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
		MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext) {

			@Override
			protected DBRef createDBRef(Object target, MongoPersistentProperty property) {
				// we should set id before save
				if (target instanceof BaseDocument) {
					if (((BaseDocument) target).getId() == null) {
						((BaseDocument) target).setId(IdGenerator.nextId());
					}
				}
				return super.createDBRef(target, property);
			}

			protected void writeInternal(@Nullable Object obj, Bson bson, @Nullable TypeInformation<?> typeHint) {
				// we should set id before save
				log.debug("Write internal for %s", obj);
				if (obj instanceof BaseDocument) {
					if (((BaseDocument) obj).getId() == null) {
						((BaseDocument) obj).setId(IdGenerator.nextId());
					}
					if (((BaseDocument) obj).getCreatedDate() == null) {
						((BaseDocument) obj).setCreatedDate(OffsetDateTime.now(ZoneOffset.UTC));
					}
					((BaseDocument) obj).setUpdatedDate(OffsetDateTime.now(ZoneOffset.UTC));
				}
				super.writeInternal(obj, bson, typeHint);
			}

		};

		mongoConverter.setCustomConversions(new CustomConversions(customConversions()));

		return mongoConverter;
	}

	protected List<Converter<?, ?>> customConversions() {
		List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
		converterList.add(new DateToOffsetDateTimeConverter());
		converterList.add(new OffsetDateTimeToDateConverter());
		return converterList;
	}

	;

	@Bean
	public SaveWithIdMongoEventListener saveWithIdMongoEventListener() {
		return new SaveWithIdMongoEventListener();
	}

	@Bean
	public CascadeSaveMongoEventListener cascadeSaveMongoEventListener() {
		return new CascadeSaveMongoEventListener();
	}
}
