package tech.shooting.commons.constraints;

public class Version {

	public static final String NOT_INCLUDE_VERSION_REGEXP = "^((?!v[0123456789.]*).)*$";

	public static final String INCLUDE_VERSION_REGEXP = ".*/v[0123456789.]*/.*";
}
