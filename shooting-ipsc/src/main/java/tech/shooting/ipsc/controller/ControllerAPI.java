package tech.shooting.ipsc.controller;

public class ControllerAPI {
	
	public static final String VERSION_1_0 = "/v1.0";
	
    public static final String CONTROLLER_GENERAL_REQUEST = "";
    public static final String CONTROLLER_SPECIFIC_REQUEST = "/{id}";

	public static final String AUTH_CONTROLLER = "/api/auth";
	public static final String AUTH_CONTROLLER_POST_LOGIN = "/login";
	public static final String AUTH_CONTROLLER_POST_LOGOUT = "/logout";
	public static final String AUTH_CONTROLLER_GET_STATUS = "/status";
	public static final String AUTH_CONTROLLER_POST_STATUS = "/status";
	
    public static final String USER_CONTROLLER = "/api/users";
    public static final String USER_CONTROLLER_PUT_UPDATE = "/{userId}";
    public static final String USER_CONTROLLER_GET_USER = "/{userId}";
    public static final String USER_CONTROLLER_DELETE_USER = "/{userId}";
    public static final String USER_CONTROLLER_CHANGE_PASSWORD = "/changepassword";
    public static final String USER_CONTROLLER_GET_ALL_USERS_BY_PAGE = "/all/{pageSize}/from/{pageNumber}";
    public static final String USER_CONTROLLER_GET_ALL = "/all";
    public static final String USER_CONTROLLER_POST_CREATE = "/create";
}
