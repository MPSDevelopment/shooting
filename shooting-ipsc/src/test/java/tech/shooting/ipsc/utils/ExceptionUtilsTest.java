package tech.shooting.ipsc.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
