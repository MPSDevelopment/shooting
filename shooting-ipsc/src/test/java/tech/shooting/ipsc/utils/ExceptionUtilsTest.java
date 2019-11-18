package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import tech.shooting.commons.constraints.IpscConstants;

@Tag(IpscConstants.UNIT_TEST_TAG)
public class ExceptionUtilsTest {

	@Test
	public void getDuplicateKeyErrorField() {
		assertEquals("serialNumber", ExceptionUtils.getDuplicateKeyErrorField("index: serialNumber dup key: { : \"AD32434234\" }"));
	}
	
	@Test
	public void getDuplicateKeyErrorValue() {
		assertEquals("AD32434234", ExceptionUtils.getDuplicateKeyErrorValue("index: serialNumber dup key: { : \"AD32434234\" }"));
	}
}
