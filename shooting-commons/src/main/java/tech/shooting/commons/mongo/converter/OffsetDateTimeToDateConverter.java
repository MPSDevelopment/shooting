package tech.shooting.commons.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

	@Override
	public Date convert(OffsetDateTime source) {
		Date result = source == null ? null : Date.from(source.toInstant());
		log.debug("Converting offsetdatetime %s to date %s", source, result);
		return result;
	}

//	@Override
//	public Date convert(OffsetDateTime source) {
//		Date result = source == null ? null : Date.from(source.toInstant());
//		DateTimeZone zone = DateTimeZone.getDefault();
//		long utcLong = zone.convertLocalToUTC(result.getTime(), false);
//		result = new Date(utcLong);
//		log.debug("Converting offsetdatetime %s to date %s", source, result);
//		return result;
//	}

}