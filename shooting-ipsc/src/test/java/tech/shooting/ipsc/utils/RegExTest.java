package tech.shooting.ipsc.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import tech.shooting.commons.constraints.IpscConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(IpscConstants.UNIT_TEST_TAG)
public class RegExTest {

	private Matcher matcher;

	@Test
	public void checkSwagger() {
		Pattern pattern = Pattern.compile(".*/v[0123456789.]*/.*");
		Matcher matcher = pattern.matcher("/operations/v1.0/gufi");
		assertTrue(matcher.find());
		matcher = pattern.matcher("/operations/v2.0/gufi");
		assertTrue(matcher.find());
		pattern = Pattern.compile("^((?!v[0123456789.]*).)*$");
		matcher = pattern.matcher("/operations/v1.0/gufi");
		assertFalse(matcher.find());
		matcher = pattern.matcher("/operations/v2.0/gufi");
		assertFalse(matcher.find());
		matcher = pattern.matcher("/operations/v1.1/gufi");
		assertFalse(matcher.find());
		matcher = pattern.matcher("/operations/gufi");
		assertTrue(matcher.find());
	}

	@Test
	public void checkAnswer() {
		Pattern pattern = Pattern.compile("^((?!state|government|head).)*$");
		String s = "state of";
		matcher = pattern.matcher(s);
		assertFalse(matcher.find());
		s = "government of";
		matcher = pattern.matcher(s);
		assertFalse(matcher.find());
		s = "Abc of";
		matcher = pattern.matcher(s);
		assertTrue(matcher.find());
		s = "Operation of state correct";
		matcher = pattern.matcher(s);
		assertFalse(matcher.find());
	}

	@Test
	public void checkApi() {
		Pattern pattern = Pattern.compile("^(?!/(api|doc|swagger|webjars|image|error)).*$");
		String s = "/api/auth";
		matcher = pattern.matcher(s);
		assertFalse(matcher.find());
		matcher = pattern.matcher("/index.html");
		assertTrue(matcher.find());
	}

	@Test
	public void checkUserName() {
		Pattern pattern = Pattern.compile("[^0-9+-.]+");
		matcher = pattern.matcher("Thor");
		assertTrue(matcher.find());
		matcher = pattern.matcher("1234");
		assertFalse(matcher.find());
		matcher = pattern.matcher("12Thor23");
		assertTrue(matcher.find());
		matcher = pattern.matcher("98Thowwr");
		assertTrue(matcher.find());
		matcher = pattern.matcher("9.8");
		assertFalse(matcher.find());
		matcher = pattern.matcher("9.8+-");
		assertFalse(matcher.find());
		matcher = pattern.matcher("9.8+-t");
		assertTrue(matcher.find());
	}
}
