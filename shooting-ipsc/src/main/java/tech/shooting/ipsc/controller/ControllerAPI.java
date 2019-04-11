package tech.shooting.ipsc.controller;

public class ControllerAPI {
	public static final String VERSION_1_0 = "/v1.0";

	/*
	 * header variable
	 */
	public static final String HEADER_VARIABLE_TOTAL = "total";

	public static final String HEADER_VARIABLE_PAGE = "page";

	public static final String HEADER_VARIABLE_PAGES = "pages";

	/*
	 * path variable
	 */
	
	public static final String PATH_VARIABLE_ID = "id";
	
	public static final String PATH_VARIABLE_COMPETITION_ID = "competitionId";

	public static final String PATH_VARIABLE_STAGE_ID = "stageId";

	public static final String PATH_VARIABLE_COMPETITOR_ID = "competitorId";

	public static final String PATH_VARIABLE_PAGE_NUMBER = "pageNumber";

	public static final String PATH_VARIABLE_PAGE_SIZE = "pageSize";

	public static final String PATH_VARIABLE_PERSON_ID = "personId";

	public static final String PATH_VARIABLE_USER_ID = "userId";

	public static final String PATH_VARIABLE_DIVISION_ID = "divisionId";

	public static final String PATH_VARIABLE_QUIZ_ID = "quizId";

	public static final String PATH_VARIABLE_QUESTION_ID = "questionId";

	public static final String PATH_VARIABLE_SUBJECT = "subject";
	
	

	/*
	 * request variable
	 */
	public static final String REQUEST_ID = "{" + PATH_VARIABLE_ID + "}";
	
	public static final String REQUEST_DIVISION_ID = "{" + PATH_VARIABLE_DIVISION_ID + "}";

	public static final String REQUEST_SUBJECT = "{" + PATH_VARIABLE_SUBJECT + "}";

	public static final String REQUEST_COMPETITION_ID = "{" + PATH_VARIABLE_COMPETITION_ID + "}";

	public static final String REQUEST_STAGE_ID = "{" + PATH_VARIABLE_STAGE_ID + "}";

	public static final String REQUEST_COMPETITOR_ID = "{" + PATH_VARIABLE_COMPETITOR_ID + "}";

	public static final String REQUEST_PAGE_NUMBER = "{" + PATH_VARIABLE_PAGE_NUMBER + "}";

	public static final String REQUEST_PAGE_SIZE = "{" + PATH_VARIABLE_PAGE_SIZE + "}";

	public static final String REQUEST_PERSON_ID = "{" + PATH_VARIABLE_PERSON_ID + "}";

	public static final String REQUEST_USER_ID = "{" + PATH_VARIABLE_USER_ID + "}";

	public static final String REQUEST_QUIZ_ID = "{" + PATH_VARIABLE_QUIZ_ID + "}";

	public static final String REQUEST_QUESTION_ID = "{" + PATH_VARIABLE_QUESTION_ID + "}";

	/*
	 * Image controller
	 */
	
	public static final String IMAGE_CONTROLLER = "/api/images";
	
	public static final String IMAGE_CONTROLLER_GET_DATA = "/data/{id}";

	/*
	 * auth controller
	 */

	public static final String AUTH_CONTROLLER = "/api/auth";

	public static final String AUTH_CONTROLLER_POST_LOGIN = "/login";

	public static final String AUTH_CONTROLLER_POST_LOGOUT = "/logout";

	public static final String AUTH_CONTROLLER_GET_STATUS = "/status";

	public static final String AUTH_CONTROLLER_POST_STATUS = "/status";

	/*
	 * user controller
	 */

	public static final String USER_CONTROLLER = "/api/users";

	public static final String USER_CONTROLLER_PUT_USER = "/" + REQUEST_USER_ID;

	public static final String USER_CONTROLLER_GET_USER = "/" + REQUEST_USER_ID;

	public static final String USER_CONTROLLER_DELETE_USER = "/" + REQUEST_USER_ID;

	public static final String USER_CONTROLLER_CHANGE_PASSWORD = "/changepassword/" + REQUEST_USER_ID;

	public static final String USER_CONTROLLER_GET_USERS_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	public static final String USER_CONTROLLER_GET_USERS = "/all";

	public static final String USER_CONTROLLER_GET_COUNT = "/count";

	public static final String USER_CONTROLLER_POST_USER = "/create";

	public static final String USER_CONTROLLER_GET_JUDGES = "/all/judge";

	/*
	 * person controller
	 */

	public static final String PERSON_CONTROLLER = "/api/persons";

	public static final String PERSON_CONTROLLER_GET_PERSON = "/" + REQUEST_PERSON_ID;

	public static final String PERSON_CONTROLLER_POST_PERSON = "/create";

	public static final String PERSON_CONTROLLER_PUT_PERSON = "/" + REQUEST_PERSON_ID;

	public static final String PERSON_CONTROLLER_DELETE_PERSON = "/" + REQUEST_PERSON_ID;

	public static final String PERSON_CONTROLLER_GET_PERSONS = "/all";

	public static final String PERSON_CONTROLLER_GET_USERS_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	public static final String PERSON_CONTROLLER_GET_COUNT = "/count";

	/*
	 * competition controller
	 */
	public static final String COMPETITION_CONTROLLER = "/api/competitions";

	public static final String COMPETITION_CONTROLLER_POST_COMPETITION = "/create";

	public static final String COMPETITION_CONTROLLER_GET_COMPETITION = "/" + REQUEST_COMPETITION_ID;

	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITION = "/" + REQUEST_COMPETITION_ID;

	public static final String COMPETITION_CONTROLLER_PUT_COMPETITION = "/" + REQUEST_COMPETITION_ID;

	public static final String COMPETITION_CONTROLLER_GET_COUNT = "/count";

	public static final String COMPETITION_CONTROLLER_GET_COMPETITIONS = "/all";

	public static final String COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	/*
	 * competition stage
	 */
	public static final String COMPETITION_CONTROLLER_GET_STAGES = "/" + REQUEST_COMPETITION_ID + "/stage/all";

	public static final String COMPETITION_CONTROLLER_POST_STAGES = "/" + REQUEST_COMPETITION_ID + "/stages";

	public static final String COMPETITION_CONTROLLER_POST_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage";

	public static final String COMPETITION_CONTROLLER_GET_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;

	public static final String COMPETITION_CONTROLLER_DELETE_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;

	public static final String COMPETITION_CONTROLLER_PUT_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;

	/*
	 * competition competitor
	 */
	public static final String COMPETITION_CONTROLLER_GET_COMPETITORS = "/" + REQUEST_COMPETITION_ID + "/competitor/all";

	public static final String COMPETITION_CONTROLLER_POST_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor";

	public static final String COMPETITION_CONTROLLER_GET_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;

	public static final String COMPETITION_CONTROLLER_PUT_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;

	public static final String COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID + "/judge";

	public static final String COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK_BOTH = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID + "/judge/both";

	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;

	public static final String COMPETITION_CONTROLLER_POST_LIST_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitors";

	/*
	 * division controller
	 */
	public static final String DIVISION_CONTROLLER = "/api/divisions";

	public static final String DIVISION_CONTROLLER_GET_ALL = "/division/all";

	public static final String DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	public static final String DIVISION_CONTROLLER_POST_DIVISION = "/division/create";

	public static final String DIVISION_CONTROLLER_DELETE_DIVISION = "/division/" + REQUEST_DIVISION_ID;

	public static final String DIVISION_CONTROLLER_GET_DIVISION_BY_ID = "/division/" + REQUEST_DIVISION_ID;

	public static final String DIVISION_CONTROLLER_PUT_DIVISION = "/division";

	public static final String DIVISION_CONTROLLER_GET_DIVISION_ROOT = "/division/root";

	/*
	 * enum
	 */
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM = "/enum";

	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON = "/enum/weapon";

	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL = "/enum/level";

	public static final String PERSON_CONTROLLER_GET_PRESENT_ENUM = "/enum/present";

	public static final String PERSON_CONTROLLER_GET_TYPE_MARK_ENUM = "/enum/mark";

	public static final String VALIDATION_CONTROLLER = "/api/validation";

	public static final String VALIDATION_CONTROLLER_GET_VALIDATIONS = "/";

	public static final String COMPETITION_CONTROLLER_POST_SCORE = "/competition/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID + "/score";

	public static final String COMPETITION_CONTROLLER_POST_SCORE_LIST = "/competition/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID + "/score/list";

	public static final String PERSON_CONTROLLER_GET_TYPE_DISQUALIFICATION_ENUM = "/enum/disqualification";

	public static final String QUIZ_CONTROLLER_GET_SUBJECTS_ENUM = "/enum/subjects";

	// quiz section

	public static final String QUIZ_CONTROLLER = "/api/quiz";

	public static final String QUIZ_CONTROLLER_POST_QUIZ = "/quiz/create";

	public static final String QUIZ_CONTROLLER_GET_ALL_QUIZ = "/quiz/all";

	public static final String QUIZ_CONTROLLER_GET_QUIZ = "/quiz/" + REQUEST_QUIZ_ID;

	public static final String QUIZ_CONTROLLER_PUT_QUIZ = "/quiz/" + REQUEST_QUIZ_ID;

	public static final String QUIZ_CONTROLLER_DELETE_QUIZ = "/quiz/" + REQUEST_QUIZ_ID;

	public static final String QUIZ_CONTROLLER_POST_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/question";

	public static final String QUIZ_CONTROLLER_GET_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/question/" + REQUEST_QUESTION_ID;

	public static final String QUIZ_CONTROLLER_DELETE_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/question/" + REQUEST_QUESTION_ID;

	public static final String QUIZ_CONTROLLER_PUT_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/question/" + REQUEST_QUESTION_ID;

	public static final String QUIZ_CONTROLLER_GET_SUBJECT_QUIZ = "/quiz/subject/" + REQUEST_SUBJECT;

	public static final String QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	public static final String QUIZ_CONTROLLER_POST_ANSWER_TO_QUIZ = "/report/create";

	// checkin section
	public static final String CHECKIN_CONTROLLER = "/api/checkin";

	public static final String CHECKIN_CONTROLLER_POST_CHECK = "/check";

	public static final String CHECKIN_CONTROLLER_GET_BY_DIVISION = "/check/division/" + REQUEST_DIVISION_ID;

	public static final String CHECKIN_CONTROLLER_POST_COMBAT_NOTE = "/check/division/" + REQUEST_DIVISION_ID + "/combatnote";

	public static final String CHECKIN_CONTROLLER_GET_COMBAT_NOTE = "/check/division/" + REQUEST_DIVISION_ID + "/combatnote";

	public static final String QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/list";

	public static final String QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK = "/quiz/" + REQUEST_QUIZ_ID + "/list/check";
}
