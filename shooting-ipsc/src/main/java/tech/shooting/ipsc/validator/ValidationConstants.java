package tech.shooting.ipsc.validator;

public class ValidationConstants {
	public static final String PHONE_PATTERN = "^[\\+]?([0-9]{1,2})?[-\\s]?[(]?[0-9]{3}[)]?[-\\s]?[0-9]{3}[-\\s]?[0-9]{2,4}[-\\s]?[0-9]{0,2}$";

	public static final String USER_ID_MESSAGE = "Required";

	public static final String USER_PHONE_MESSAGE = "Min 7 characters (digits - and +)";

	public static final String USER_NAME_MESSAGE = "Min 3 character";
	
	public static final String USER_LOGIN_MESSAGE = "Min 3 character";

	public static final String USER_INCORRECT_LOGIN_MESSAGE = "Incorrect login";

	public static final String USER_LOGIN_REQUIRED_MESSAGE = "Incorrect login";

	public static final String USER_PASSWORD_MESSAGE = "Min 4 characters";

	public static final String USER_PASSWORD_REQUIRED_MESSAGE = "Required";

	public static final String USER_OLD_PASSWORD_MATCH_MESSAGE = "The old password is not correct";

	public static final String USER_DOES_NOT_EXIST_MESSAGE = "User does not exist";

	public static final String LOGIN_INCORRECT = "The account or password is incorrect. Please try again";

	public static final String PERSON_NAME_MESSAGE = "Min 3 character";
	
	public static final String LEVEL_MESSAGE = "Level required";

	public static final String COMPETITION_NAME_MESSAGE = "Name min 5, max 50 character";

	public static final String COMPETITION_LOCATION_MESSAGE = "Location min 5, max 50 character";

	public static final String STAGE_NAME_MESSAGE = " Stage name min 5 , max 50 character";

	public static final String STAGE_TARGETS_COUNT_MESSAGE = "Count of targets must be min 1";

	public static final String STAGE_ROUND_COUNT_MESSAGE = "Count of rounds must be min 1";

	public static final String STAGE_STAGE_MAXIMUM_POINTS_MESSAGE = "Stage maximum points must be min 5";

	public static final String DIVISION_NAME_MESSAGE = "Division name must be min 3 characters";

	public static final String STAGE_ID = "Stage id is required, must be not null";

	public static final String STAGE_ID_POSITIVE = "Stage id is required, must be positive";

	public static final String PERSON_ID = "Person id is required, must be not null";

	public static final String PERSON_ID_POSITIVE = "Person id is required, must be positive";

	public static final String SCORE_MESSAGE = "Must be not null";

	public static final String TIME_MESSAGE = "The time must be not null and positive";

	public static final String SATISFACTIORILY_MARK_MESSAGE = "The satisfactorily point must be not null and positive";

	public static final String SUBJECT_MESSAGE = "The subject id  must be not null";

	public static final String GREAT_MARK_MESSAGE = "The great point must be not null and positive";

	public static final String GOOD_MARK_MESSAGE = "The good point must be not null and positive";

	public static final String QUIZ_MESSAGE = "The quiz name must be not null and more than 3 characters";
}
