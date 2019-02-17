package tech.shooting.commons.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
public class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(Date source) {
        OffsetDateTime result = source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
        log.debug("Converting date %s to offsetdatetime %s", source, result);
        return result;
    }


//	@Override
//	public OffsetDateTime convert(Date source) {
//		DateTimeZone zone = DateTimeZone.getDefault();
//		long utcLong = zone.convertUTCToLocal(source.getTime());
//		OffsetDateTime result = source == null ? null : OffsetDateTime.ofInstant(new Date(utcLong).toInstant(), ZoneOffset.UTC);
//		log.debug("Converting date %s to offsetdatetime %s", source, result);
//		return result;
//	}

}