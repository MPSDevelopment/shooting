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

//	public static final String COMPETITION_NAME_MESSAGE = "Name min 3, max 50 character";
	
	public static final String COMPETITION_NAME_MESSAGE = "Name required";

//	public static final String COMPETITION_LOCATION_MESSAGE = "Location min 5, max 50 character";
	
	public static final String COMPETITION_LOCATION_MESSAGE = "Location required";

//	public static final String STAGE_NAME_MESSAGE = " Stage name min 5 , max 50 character";
	
	public static final String STAGE_NAME_MESSAGE = " Stage name required";

	public static final String STAGE_TARGETS_COUNT_MESSAGE = "Count of targets must be min 1";

	public static final String STAGE_ROUND_COUNT_MESSAGE = "Count of rounds must be min 1";

	public static final String STAGE_STAGE_MAXIMUM_POINTS_MESSAGE = "Stage maximum points must be min 5";

	public static final String DIVISION_NAME_MESSAGE = "Division name must be min 3 characters";

	public static final String STAGE_ID = "Stage id is required, must be not null";

	public static final String STAGE_ID_POSITIVE = "Stage id is required, must be positive";

	public static final String PERSON_ID = "Person id is required, must be not null";

	public static final String PERSON_ID_POSITIVE = "Person id is required, must be positive";

	public static final String SCORE_MESSAGE = "Must be not null";
	
	public static final String STANDARD_ID = "Standard id is required, must be not null";

	public static final String TIME_MESSAGE = "The time must be not null and positive";

	public static final String SATISFACTORILY_MARK_MESSAGE = "The satisfactorily point must be not null and positive";

	public static final String SUBJECT_MESSAGE = "The subject id  must be not null";

	public static final String GREAT_MARK_MESSAGE = "The great point must be not null and positive";

	public static final String GOOD_MARK_MESSAGE = "The good point must be not null and positive";

	public static final String QUIZ_KZ_MESSAGE = "The quiz name in KZ must be not null and more than 3 characters";

	public static final String QUIZ_RUS_MESSAGE = "The quiz name in RUS must be not null and more than 3 characters";

	public static final String DATE_MESSAGE = "Date must be not null";

	public static final String QUIZ_ID_MESSAGE = "Quiz id must be not null";

	public static final String QUESTION_ID_MESSAGE = "Question id must be not null";

	public static final String ANSWER_MESSAGE = "Answer must be nor null";

	public static final String ANSWER_MESSAGE_POSITIVE_OR_ZERO = "Answer must be 0 or positive value";

	public static final String ANSWERS_SIZE_MESSAGE = "Size of answers list be 4 or greater";

	public static final String NUMBER_MIN_MESSAGE = "Number must be min 1";

	public static final String NUMBER_MAX_MESSAGE = "Number must be max 4";
	public static final String SPECIALITY_MESSAGE = "The speciality length must be not blank";
	public static final String SPECIALITY_SIZE_MESSAGE = "The speciality length must be min 3 characters";
	public static final String WEAPON_COUNT_MESSAGE = "Must be 0 or greater";
	public static final String WEAPON_SERIAL_NUMBER_MESSAGE = "Serial number 7 characters min";
	public static final String WEAPON_NAME_MESSAGE = "Weapon type name must be not blank";
	public static final String COMMUNICATION_EQUIPMENT_NAME_MESSAGE = "Communication equipment type name must be not blank";
	public static final String COMMUNICATION_EQUIPMENT_SERIAL_NUMBER_MESSAGE = "Serial number 7 characters min";
	public static final String VEHICLE_NAME_MESSAGE = "Vehicle type name must be not blank";
	public static final String VEHICLE_SERIAL_NUMBER_MESSAGE = "Serial number 7 characters min";
	public static final String PASSPORT_NUMBER_NUMBER_MESSAGE = "Passport number 7 characters min";
	public static final String UNIT_NAME_MESSAGE = "Unit name must be not blank";
	public static final String COURSE_NAME_MESSAGE = "Course name must be min 3 characters";
}
