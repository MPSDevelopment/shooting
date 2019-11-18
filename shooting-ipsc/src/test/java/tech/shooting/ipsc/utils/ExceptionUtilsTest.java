package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import tech.shooting.commons.constraints.IpscConstants;

@Tag(IpscConstants.UNIT_TEST_TAG)
public class ExceptionUtilsTest {

	@Test
	public void getDuplicateKeyErrorField() {
		assertEquals("passportNumber", ExceptionUtils.getDuplicateKeyErrorField("E11000 duplicate key error collection: shooting.vehicle index: passportNumber dup key: { : \"2342\" }; nested exception is "));
	}
	
	@Test
	public void getDuplicateKeyErrorValue() {
		assertEquals("2342", ExceptionUtils.getDuplicateKeyErrorValue("E11000 duplicate key error collection: shooting.vehicle index: passportNumber dup key: { : \"2342\" }; nested exception is "));
	}
}
