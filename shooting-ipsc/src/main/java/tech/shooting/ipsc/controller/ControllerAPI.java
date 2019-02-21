package tech.shooting.ipsc.controller;

public class ControllerAPI {
	
	public static final String VERSION_1_0 = "/v1.0";
	
    public static final String CONTROLLER_GENERAL_REQUEST = "";
    public static final String CONTROLLER_SPECIFIC_REQUEST = "/{id}";

	public static final String AUTH_CONTROLLER = "/api/auth";
	public static final String AUTH_CONTROLLER_POST_LOGIN = "/login";
	public static final String AUTH_CONTROLLER_POST_LOGOUT = "/logout";
	public static final String AUTH_CONTROLLER_GET_STATUS = "/status";
}
