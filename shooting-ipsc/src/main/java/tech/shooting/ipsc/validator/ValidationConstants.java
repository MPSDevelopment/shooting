package tech.shooting.ipsc.validator;

public class ValidationConstants {
	
	public static final String PHONE_PATTERN =  "^[\\+]?([0-9]{1,2})?[-\\s]?[(]?[0-9]{3}[)]?[-\\s]?[0-9]{3}[-\\s]?[0-9]{2,4}[-\\s]?[0-9]{0,2}$";
	
	public static final String USER_ID_MESSAGE = "Required";
	
	public static final String USER_PHONE_MESSAGE = "Min 7 characters (digits - and +)";

	public static final String USER_LAST_NAME_MESSAGE = "Min 3 character";

	public static final String USER_FIRST_NAME_MESSAGE = "Min 3 character";

	public static final String USER_EMAIL_MESSAGE = "Incorrect email";
	
	public static final String USER_EMAIL_REQUIRED_MESSAGE = "Incorrect email";

	public static final String USER_PASSWORD_MESSAGE = "Min 5 characters";
	
	public static final String USER_PASSWORD_REQUIRED_MESSAGE = "Required";
	
	public static final String USER_OLD_PASSWORD_MATCH_MESSAGE = "The old password is not correct";
	
	public static final String USER_DOES_NOT_EXIST_MESSAGE = "User does not exist";

	public static final String LOGIN_INCORRECT = "The account or password is incorrect. Please try again";

}
