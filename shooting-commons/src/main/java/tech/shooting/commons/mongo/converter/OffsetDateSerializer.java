package tech.shooting.commons.mongo.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import tech.shooting.commons.utils.OffsetDateUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

public class OffsetDateSerializer extends JsonSerializer<OffsetDateTime> {

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(OffsetDateUtils.parseDateTimeToString(value));
	}
}
