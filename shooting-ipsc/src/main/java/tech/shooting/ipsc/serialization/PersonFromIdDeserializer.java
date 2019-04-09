package tech.shooting.ipsc.serialization;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.PersonRepository;

@Component
public class PersonFromIdDeserializer extends JsonDeserializer<Person> {

	@Autowired
	private PersonRepository personRepository;

	@Override
	public Person deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		
		JsonToken currentToken = jp.getCurrentToken();

		String currentName = jp.getCurrentName();

		if (currentToken.equals(JsonToken.VALUE_STRING)) {
			return personRepository.findById(Long.valueOf(currentToken.asString())).orElseThrow(() -> new ValidationException(currentName, "Cannot get person from the database by id %s", currentToken));
		} else if (currentToken.equals(JsonToken.VALUE_NULL)) {
			return getNullValue();
		}

		throw new ValidationException(currentName, "Only string values supported. Value was %s", jp.getText());
	}

	@Override
	public Person getNullValue() {
		return null;
	}

}
