package tech.shooting.ipsc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionUtils {

	private static final String DUPLICATE_ERROR_PATTERN_FIELD = ".*: (.*) dup key:.*";
	
	private static final String DUPLICATE_ERROR_PATTERN_VALUE = ".*\\{ : \"(.*)\" \\}.*";

	public static String getDuplicateKeyErrorField(String value) {
		Pattern pattern = Pattern.compile(DUPLICATE_ERROR_PATTERN_FIELD);
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
	
	public static String getDuplicateKeyErrorValue(String value) {
		Pattern pattern = Pattern.compile(DUPLICATE_ERROR_PATTERN_VALUE);
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
