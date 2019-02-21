package tech.shooting.ipsc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RegExTest {

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
        Matcher matcher = pattern.matcher(s);
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
		Pattern pattern = Pattern.compile("^/((?!api|doc|swagger|webjars|image|error).)*$");
		String s = "/api/auth";
        Matcher matcher = pattern.matcher(s);
        assertFalse(matcher.find());
     
        matcher = pattern.matcher("/index.html");
        assertTrue(matcher.find());
	}
}
