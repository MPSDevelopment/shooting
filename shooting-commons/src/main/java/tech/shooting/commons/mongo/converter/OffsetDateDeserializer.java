package tech.shooting.commons.mongo.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.utils.OffsetDateUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

public class OffsetDateDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken.equals(JsonToken.VALUE_STRING)) {
            OffsetDateTime value = OffsetDateUtils.parseDateTimeString(jp.getText());
            if (value != null) {
                return value;
            }
        } else if (currentToken.equals(JsonToken.VALUE_NULL)) {
            return getNullValue();
        }

        throw new ValidationException(jp.getCurrentName(), "Only valid date values supported. Values was %s", jp.getText());
    }

    @Override
    public OffsetDateTime getNullValue() {
        return null;
    }

}
