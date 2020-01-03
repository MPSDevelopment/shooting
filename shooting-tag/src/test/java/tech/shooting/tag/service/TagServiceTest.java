package tech.shooting.tag.service;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

public class TagServiceTest {

	@Test
	public void checkToHex() {
		int code = 11759;
		
		String hex = Integer.toHexString(code);

		// String hex = Hex.encodeHexString(code.getBytes());
		 assertEquals("2def", hex);
	}
}
