package tech.shooting.ipsc.controller;

public class ControllerAPI {

	public static final String VERSION_1_0 = "/v1.0";

	public static final String COMPETITION_ID_REQUEST = "{competitionId}";
	public static final String STAGE_ID_REQUEST = "{stageId}";
	public static final String PAGE_NUMBER = "{pageNumber}";
	public static final String PAGE_SIZE = "{pageSize}";
	public static final String PERSON_ID = "{personId}";
	public static final String USER_ID = "{userId}";
	public static final String COMPETITOR_ID_REQUEST = "{competitorId}";

	public static final String AUTH_CONTROLLER = "/api/auth";
	public static final String AUTH_CONTROLLER_POST_LOGIN = "/login";
	public static final String AUTH_CONTROLLER_POST_LOGOUT = "/logout";
	public static final String AUTH_CONTROLLER_GET_STATUS = "/status";
	public static final String AUTH_CONTROLLER_POST_STATUS = "/status";

	public static final String USER_CONTROLLER = "/api/users";
	public static final String USER_CONTROLLER_PUT_UPDATE = "/" + USER_ID;
	public static final String USER_CONTROLLER_GET_USER = "/" + USER_ID;
	public static final String USER_CONTROLLER_DELETE_USER = "/" + USER_ID;
	public static final String USER_CONTROLLER_CHANGE_PASSWORD = "/changepassword/" + USER_ID;
	public static final String USER_CONTROLLER_GET_ALL_USERS_BY_PAGE = "/all/" + PAGE_NUMBER + "/" + PAGE_SIZE;
	public static final String USER_CONTROLLER_GET_ALL = "/all";
	public static final String USER_CONTROLLER_GET_COUNT = "/count";
	public static final String USER_CONTROLLER_POST_CREATE = "/create";
	public static final String USER_CONTROLLER_GET_ALL_JUDGES = "/all/judge";

	public static final String PERSON_CONTROLLER = "/api/persons";
	public static final String PERSON_CONTROLLER_GET_PERSON = "/" + PERSON_ID;
	public static final String PERSON_CONTROLLER_POST_CREATE = "/create";
	public static final String PERSON_CONTROLLER_PUT_UPDATE = "/" + PERSON_ID;
	public static final String PERSON_CONTROLLER_DELETE_PERSON = "/" + PERSON_ID;
	public static final String PERSON_CONTROLLER_GET_ALL = "/all";
	public static final String PERSON_CONTROLLER_GET_ALL_USERS_BY_PAGE = "/all/" + PAGE_NUMBER + "/" + PAGE_SIZE;
	public static final String PERSON_CONTROLLER_GET_COUNT = "/count";

	public static final String COMPETITION_CONTROLLER = "/api/competition";
	public static final String COMPETITION_CONTROLLER_POST_CREATE = "/create";
	public static final String COMPETITION_CONTROLLER_GET_BY_ID = "/" + COMPETITION_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_DELETE_BY_ID = "/" + COMPETITION_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_PUT_BY_ID = "/" + COMPETITION_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_GET_COUNT = "/count";
	public static final String COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS = "/all";
	public static final String COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE = "/all/" + PAGE_NUMBER + "/" + PAGE_SIZE;

	public static final String COMPETITION_CONTROLLER_GET_STAGES = "/" + COMPETITION_ID_REQUEST + "/stages";
	public static final String COMPETITION_CONTROLLER_POST_STAGES = "/" + COMPETITION_ID_REQUEST + "/stages";
	public static final String COMPETITION_CONTROLLER_POST_STAGE = "/" + COMPETITION_ID_REQUEST + "/stage";
	public static final String COMPETITION_CONTROLLER_GET_STAGE = "/" + COMPETITION_ID_REQUEST + "/" + STAGE_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_DELETE_STAGE = "/" + COMPETITION_ID_REQUEST + "/" + STAGE_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_PUT_STAGE = "/" + COMPETITION_ID_REQUEST + "/" + STAGE_ID_REQUEST;
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM = "/enum";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITORS = "/" + COMPETITION_ID_REQUEST + "/competitor/all";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITOR = "/" + COMPETITION_ID_REQUEST + "/competitor";
	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITOR = "/" + COMPETITION_ID_REQUEST + "/competitor/" + COMPETITOR_ID_REQUEST;
}
