package tech.shooting.tag.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class JacksonUtils {

	public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static ObjectMapper mapper = getMapper();

	public static ObjectMapper getMapper() {

		if (mapper != null) {
			return mapper;
		}
		return createMapper();
	}

	public static ObjectMapper createMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_STRING);
//		objectMapper.getDeserializationConfig().with(new SimpleDateFormat(DATETIME_FORMAT_STRING));
//		objectMapper.getSerializationConfig().with(new SimpleDateFormat(DATETIME_FORMAT_STRING));
		objectMapper.setDateFormat(dateFormat);

		objectMapper.configure(Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false); //  For preventing float to integer auto conversion
//		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false);
//		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false);
		
//		objectMapper.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false); For preventing string to integer auto conversion
		
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// objectMapper.configure(Feature.WRITE_NUMBERS_AS_STRINGS, true);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.DEFAULT);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.DEFAULT);
		SimpleModule module = new SimpleModule();
		module.addSerializer(long.class, new ToStringSerializer());
		module.addSerializer(Long.class, new ToStringSerializer());
		
//		module.addSerializer(OffsetDateTime.class, new OffsetDateSerializer());
//		module.addDeserializer(OffsetDateTime.class, new OffsetDateDeserializer());
		
		objectMapper.registerModule(module);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return objectMapper;
	}

	public static String getJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot get json from the object %s", object);
		}
		return null;
	}

	public static <T> T convert(Class<T> clazz, T object) {
		return fromJson(clazz, getJson(object));
	}

	public static <T> List<T> convertList(Class<T[]> clazz, T object) {
		return getListFromJson(clazz, getJson(object));
	}

	public static String getFullJson(Object object) {
		try {
			return new ObjectMapper().disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot get full json from the object %s", object);
		}
		return null;
	}

	public static String getFullPrettyJson(Object object) {
		try {
			return new ObjectMapper().disable(MapperFeature.USE_ANNOTATIONS).writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot get full pretty json from the object %s", object);
		}
		return null;
	}

	public static void getJson(Object object, File file) throws IOException {
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
	}

	public static String getJson(Class<?> view, Object object) throws JsonProcessingException {
		return mapper.writerWithView(view).writeValueAsString(object);
	}

	public static String getPrettyJson(Object object) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot get pretty json from the object %s, because %s", object, e.getMessage());
		}
		return null;
	}

	public static <T> T fromJson(Class<T> clazz, String json) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("Cannot read object %s from a json %s", clazz, json);
			log.error("", e);
		}
		return null;
	}

	public static <T> T fromJson(Class<T> clazz, File file) {
		try {
			return mapper.readValue(file, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeJsonToFile(String json, String fileName) {
		try {
			FileUtils.writeStringToFile(new File(fileName), json);
		} catch (IOException var3) {
			var3.printStackTrace();
		}
	}

	public static <T> List<T> getListFromJson(Class<T[]> clazz, String json) {
		try {
			return Arrays.asList(mapper.readValue(json, clazz));
		} catch (IOException e) {
			log.error("Cannot read object list %s from a json %s", clazz, json);
		}
		return Collections.emptyList();
	}

	public static <T> List<T> getListFromJson(Class<T[]> clazz, File file) {
		try {
			return Arrays.asList(mapper.readValue(file, clazz));
		} catch (IOException e) {
			log.error("Cannot read object %s from a file %s", clazz, file);
		}
		return Collections.emptyList();
	}

	public static <T> Map<String, T> getMapFromJson(Class<T> clazz, String json) {
		try {
			return mapper.readValue(json, new TypeReference<Map<String, T>>() {
			});
		} catch (IOException e) {
			log.error("Cannot read object %s from a json %s", clazz, json);
		}
		return Collections.emptyMap();
	}
}
