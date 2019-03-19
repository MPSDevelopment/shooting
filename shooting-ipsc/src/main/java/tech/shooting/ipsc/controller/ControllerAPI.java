package tech.shooting.ipsc.controller;

public class ControllerAPI {

	public static final String VERSION_1_0 = "/v1.0";

	public static final String PATH_VARIABLE_COMPETITION_ID = "competitionId";
	public static final String PATH_VARIABLE_STAGE_ID = "stageId";
	public static final String PATH_VARIABLE_COMPETITOR_ID = "competitorId";
	public static final String PATH_VARIABLE_PAGE_NUMBER = "pageNumber";
	public static final String PATH_VARIABLE_PAGE_SIZE = "pageSize";
	public static final String PATH_VARIABLE_PERSON_ID = "personId";
	public static final String PATH_VARIABLE_USER_ID = "userId";
	public static final String PATH_VARIABLE_DIVISION_ID = "divisionId";


	public static final String REQUEST_DIVISION_ID = "{" + PATH_VARIABLE_DIVISION_ID + "}";
	public static final String REQUEST_COMPETITION_ID = "{"+PATH_VARIABLE_COMPETITION_ID+"}";
	public static final String REQUEST_STAGE_ID = "{"+PATH_VARIABLE_STAGE_ID+"}";
	public static final String REQUEST_COMPETITOR_ID = "{"+PATH_VARIABLE_COMPETITOR_ID+"}";
	public static final String PAGE_NUMBER_REQUEST = "{"+PATH_VARIABLE_PAGE_NUMBER+"}";
	public static final String PAGE_SIZE_REQUEST = "{"+PATH_VARIABLE_PAGE_SIZE+"}";
	public static final String PERSON_ID_REQUEST = "{"+PATH_VARIABLE_PERSON_ID+"}";
	public static final String USER_ID_REQUEST = "{"+PATH_VARIABLE_USER_ID+"}";

	public static final String AUTH_CONTROLLER = "/api/auth";
	public static final String AUTH_CONTROLLER_POST_LOGIN = "/login";
	public static final String AUTH_CONTROLLER_POST_LOGOUT = "/logout";
	public static final String AUTH_CONTROLLER_GET_STATUS = "/status";
	public static final String AUTH_CONTROLLER_POST_STATUS = "/status";

	public static final String USER_CONTROLLER = "/api/users";
	public static final String USER_CONTROLLER_PUT_USER = "/" + USER_ID_REQUEST;
	public static final String USER_CONTROLLER_GET_USER = "/" + USER_ID_REQUEST;
	public static final String USER_CONTROLLER_DELETE_USER = "/" + USER_ID_REQUEST;
	public static final String USER_CONTROLLER_CHANGE_PASSWORD = "/changepassword/" + USER_ID_REQUEST;
	public static final String USER_CONTROLLER_GET_USERS_BY_PAGE = "/all/" + PAGE_NUMBER_REQUEST + "/" + PAGE_SIZE_REQUEST;
	public static final String USER_CONTROLLER_GET_USERS = "/all";
	public static final String USER_CONTROLLER_GET_COUNT = "/count";
	public static final String USER_CONTROLLER_POST_USER = "/create";
	public static final String USER_CONTROLLER_GET_JUDGES = "/all/judge";

	public static final String PERSON_CONTROLLER = "/api/persons";
	public static final String PERSON_CONTROLLER_GET_PERSON = "/" + PERSON_ID_REQUEST;
	public static final String PERSON_CONTROLLER_POST_PERSON = "/create";
	public static final String PERSON_CONTROLLER_PUT_PERSON = "/" + PERSON_ID_REQUEST;
	public static final String PERSON_CONTROLLER_DELETE_PERSON = "/" + PERSON_ID_REQUEST;
	public static final String PERSON_CONTROLLER_GET_PERSONS = "/all";
	public static final String PERSON_CONTROLLER_GET_USERS_BY_PAGE = "/all/" + PAGE_NUMBER_REQUEST + "/" + PAGE_SIZE_REQUEST;
	public static final String PERSON_CONTROLLER_GET_COUNT = "/count";

	public static final String COMPETITION_CONTROLLER = "/api/competitions";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITION = "/create";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_PUT_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_GET_COUNT = "/count";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITIONS = "/all";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE = "/all/" + PAGE_NUMBER_REQUEST + "/" + PAGE_SIZE_REQUEST;

	public static final String COMPETITION_CONTROLLER_GET_STAGES = "/" + REQUEST_COMPETITION_ID + "/stage/all";
	public static final String COMPETITION_CONTROLLER_POST_STAGES = "/" + REQUEST_COMPETITION_ID + "/stages";
	public static final String COMPETITION_CONTROLLER_POST_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage";
	public static final String COMPETITION_CONTROLLER_GET_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_DELETE_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_PUT_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM = "/enum";

	public static final String COMPETITION_CONTROLLER_GET_COMPETITORS = "/" + REQUEST_COMPETITION_ID + "/competitor/all";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;
	public static final String COMPETITION_CONTROLLER_PUT_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;
	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;

	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON = "/enum/weapon";

	public static final String DIVISION_CONTROLLER = "/api/divisions";

	public static final String DIVISION_CONTROLLER_GET_ALL = "/division/all";

	public static final String DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE = "/all/" + PAGE_NUMBER_REQUEST + "/" + PAGE_SIZE_REQUEST;
	public static final String DIVISION_CONTROLLER_POST_DIVISION = "/division/create";
	public static final String DIVISION_CONTROLLER_DELETE_DIVISION = "/division/" + REQUEST_DIVISION_ID;

	public static final String DIVISION_CONTROLLER_GET_DIVISION_BY_ID = "/division/" + REQUEST_DIVISION_ID;

	public static final String DIVISION_CONTROLLER_PUT_DIVISION = "/division/" + REQUEST_DIVISION_ID;
}
