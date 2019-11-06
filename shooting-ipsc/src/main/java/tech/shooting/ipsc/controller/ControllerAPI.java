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
	public static final String PATH_VARIABLE_X = "x";
	public static final String PATH_VARIABLE_Y = "y";
	public static final String PATH_VARIABLE_Z = "z";
	public static final String PATH_VARIABLE_FIRED_COUNT = "fireCount";
	public static final String PATH_VARIABLE_COMPETITION_ID = "competitionId";
	public static final String PATH_VARIABLE_STAGE_ID = "stageId";
	public static final String PATH_VARIABLE_STANDARD_ID = "standardId";
	public static final String PATH_VARIABLE_COMMON_CONDITION_ID = "commonConditionId";
	public static final String PATH_VARIABLE_SPECIALITY_ID = "specialityId";
	public static final String PATH_VARIABLE_COMPETITOR_ID = "competitorId";
	public static final String PATH_VARIABLE_COMPETITOR_MARK = "competitorMark";
	public static final String PATH_VARIABLE_PAGE_NUMBER = "pageNumber";
	public static final String PATH_VARIABLE_PAGE_SIZE = "pageSize";
	public static final String PATH_VARIABLE_PERSON_ID = "personId";
	public static final String PATH_VARIABLE_PERSON_NAME = "personName";
	public static final String PATH_VARIABLE_USER_ID = "userId";
	public static final String PATH_VARIABLE_DIVISION_ID = "divisionId";
	public static final String PATH_VARIABLE_CATEGORY_ID = "categoryId";
	public static final String PATH_VARIABLE_UNIT_ID = "unitId";
	public static final String PATH_VARIABLE_STATUS = "status";
	public static final String PATH_VARIABLE_QUIZ_ID = "quizId";
	public static final String PATH_VARIABLE_QUESTION_ID = "questionId";
	public static final String PATH_VARIABLE_SUBJECT_ID = "subjectId";
	public static final String PATH_VARIABLE_WEAPON_ID = "weaponId";
	public static final String PATH_VARIABLE_VEHICLE_ID = "vehicleId";
	public static final String PATH_VARIABLE_EQUIPMENT_ID = "equipmentId";
	public static final String PATH_VARIABLE_TYPE_ID = "typeId";
	public static final String PATH_VARIABLE_INTERVAL = "interval";
	public static final String PATH_VARIABLE_DATE = "date";
	public static final String PATH_VARIABLE_COURSE_ID = "courseId";
	public static final String PATH_VARIABLE_MARK = "mark";

	/*
	 * request variable
	 */
	public static final String REQUEST_STATUS = "{" + PATH_VARIABLE_STATUS + "}";
	public static final String REQUEST_COURSE_ID = "{" + PATH_VARIABLE_COURSE_ID + "}";
	public static final String REQUEST_INTERVAL = "{" + PATH_VARIABLE_INTERVAL + "}";
	public static final String REQUEST_DATE = "{" + PATH_VARIABLE_DATE + "}";
	public static final String REQUEST_ID = "{" + PATH_VARIABLE_ID + "}";
	public static final String REQUEST_X = "{" + PATH_VARIABLE_X + "}";
	public static final String REQUEST_Y = "{" + PATH_VARIABLE_Y + "}";
	public static final String REQUEST_Z = "{" + PATH_VARIABLE_Z + "}";
	public static final String REQUEST_FIRED_COUNT = "{" + PATH_VARIABLE_FIRED_COUNT + "}";
	public static final String REQUEST_WEAPON_ID = "{" + PATH_VARIABLE_WEAPON_ID + "}";
	public static final String REQUEST_VEHICLE_ID = "{" + PATH_VARIABLE_VEHICLE_ID + "}";
	public static final String REQUEST_EQUIPMENT_ID = "{" + PATH_VARIABLE_EQUIPMENT_ID + "}";
	public static final String REQUEST_TYPE_ID = "{" + PATH_VARIABLE_TYPE_ID + "}";
	public static final String REQUEST_DIVISION_ID = "{" + PATH_VARIABLE_DIVISION_ID + "}";
	public static final String REQUEST_CATEGORY_ID = "{" + PATH_VARIABLE_CATEGORY_ID + "}";
	public static final String REQUEST_STANDARD_ID = "{" + PATH_VARIABLE_STANDARD_ID + "}";
	public static final String REQUEST_COMMON_CONDITION_ID = "{" + PATH_VARIABLE_COMMON_CONDITION_ID + "}";
	public static final String REQUEST_UNIT_ID = "{" + PATH_VARIABLE_UNIT_ID + "}";
	public static final String REQUEST_SPECIALITY_ID = "{" + PATH_VARIABLE_SPECIALITY_ID + "}";
	public static final String REQUEST_SUBJECT_ID = "{" + PATH_VARIABLE_SUBJECT_ID + "}";
	public static final String REQUEST_COMPETITION_ID = "{" + PATH_VARIABLE_COMPETITION_ID + "}";
	public static final String REQUEST_STAGE_ID = "{" + PATH_VARIABLE_STAGE_ID + "}";
	public static final String REQUEST_COMPETITOR_ID = "{" + PATH_VARIABLE_COMPETITOR_ID + "}";
	public static final String REQUEST_COMPETITOR_MARK = "{" + PATH_VARIABLE_COMPETITOR_MARK + "}";
	public static final String REQUEST_PAGE_NUMBER = "{" + PATH_VARIABLE_PAGE_NUMBER + "}";
	public static final String REQUEST_PAGE_SIZE = "{" + PATH_VARIABLE_PAGE_SIZE + "}";
	public static final String REQUEST_PERSON_ID = "{" + PATH_VARIABLE_PERSON_ID + "}";
	public static final String REQUEST_PERSON_NAME = "{" + PATH_VARIABLE_PERSON_NAME + "}";
	public static final String REQUEST_USER_ID = "{" + PATH_VARIABLE_USER_ID + "}";
	public static final String REQUEST_QUIZ_ID = "{" + PATH_VARIABLE_QUIZ_ID + "}";
	public static final String REQUEST_QUESTION_ID = "{" + PATH_VARIABLE_QUESTION_ID + "}";
	public static final String REQUEST_MARK = "{" + PATH_VARIABLE_MARK + "}";

	/*
	 * VALIDATION_CONTROLLER
	 */
	public static final String VALIDATION_CONTROLLER = "/api/validation";
	public static final String VALIDATION_CONTROLLER_GET_VALIDATIONS = "/";

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
	public static final String USER_CONTROLLER_POST_JUDGE = "/create";
	public static final String USER_CONTROLLER_POST_USER = "/createUser";
	public static final String USER_CONTROLLER_GET_JUDGES = "/all/judge";
	public static final String USER_CONTROLLER_GET_USER_ROLE = "/all/user";
	/*
	 * person controller
	 */

	public static final String PERSON_CONTROLLER = "/api/persons";
	public static final String PERSON_CONTROLLER_GET_PERSON = "/" + REQUEST_PERSON_ID;
	public static final String PERSON_CONTROLLER_GET_PERSON_BY_RFID_CODE = "/rfid/" + REQUEST_MARK;
	public static final String PERSON_CONTROLLER_GET_PERSON_BY_NUMBER = "/number/" + REQUEST_MARK;
	public static final String PERSON_CONTROLLER_GET_PERSON_BY_CALL = "/call/" + REQUEST_PERSON_ID;
	public static final String PERSON_CONTROLLER_GET_FREE_RFID = "/freerfid";
	public static final String PERSON_CONTROLLER_GET_FREE_NUMBER = "/freenumber";
	public static final String PERSON_CONTROLLER_POST_PERSON = "/create";
	public static final String PERSON_CONTROLLER_PUT_PERSON = "/" + REQUEST_PERSON_ID;
	public static final String PERSON_CONTROLLER_PUT_PERSON_RFID = "/rfid";
	public static final String PERSON_CONTROLLER_DELETE_PERSON = "/" + REQUEST_PERSON_ID;
	public static final String PERSON_CONTROLLER_GET_PERSONS = "/all";
	public static final String PERSON_CONTROLLER_GET_USERS_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String PERSON_CONTROLLER_GET_USERS_BY_DIVISION_BY_PAGE = "/all/" + REQUEST_DIVISION_ID + "/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String PERSON_CONTROLLER_GET_COUNT = "/count";
	public static final String PERSON_CONTROLLER_GET_ALL_BY_DIVISION_ID = "/all/by/division/" + REQUEST_DIVISION_ID;
	public static final String PERSON_CONTROLLER_GET_PRESENT_ENUM = "/enum/present";
	public static final String PERSON_CONTROLLER_GET_TYPE_MARK_ENUM = "/enum/mark";
	public static final String PERSON_CONTROLLER_GET_TYPE_DISQUALIFICATION_ENUM = "/enum/disqualification";
	public static final String PERSON_CONTROLLER_GET_TYPE_CLASS_ENUM = "/enum/class";
	/*
	 * competition controller
	 */
	public static final String COMPETITION_CONTROLLER = "/api/competitions";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITION = "/create";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_DELETE_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_PUT_COMPETITION = "/" + REQUEST_COMPETITION_ID;
	public static final String COMPETITION_CONTROLLER_POST_COMPETITION_START = "/competition/" + REQUEST_COMPETITION_ID + "/start";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITION_STOP = "/competition/" + REQUEST_COMPETITION_ID + "/stop";
	public static final String COMPETITION_CONTROLLER_GET_COUNT = "/count";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITIONS = "/all";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String COMPETITION_CONTROLLER_POST_SCORE = "/competition/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID + "/score";
	public static final String COMPETITION_CONTROLLER_POST_SCORE_LIST = "/competition/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID + "/score/list";
	public static final String COMPETITION_CONTROLLER_DELETE_ALL_COMPETITION = "/competition/delete/all";

	/*
	 * competition stage
	 */
	public static final String COMPETITION_CONTROLLER_GET_STAGES = "/" + REQUEST_COMPETITION_ID + "/stage/all";
	public static final String COMPETITION_CONTROLLER_POST_STAGES = "/" + REQUEST_COMPETITION_ID + "/stages";
	public static final String COMPETITION_CONTROLLER_POST_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage";
	public static final String COMPETITION_CONTROLLER_GET_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_DELETE_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_PUT_STAGE = "/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID;
	public static final String COMPETITION_CONTROLLER_GET_SCORE_LIST_BY_STAGE = "/competition/" + REQUEST_COMPETITION_ID + "/stage/" + REQUEST_STAGE_ID + "/score/list";
	public static final String COMPETITION_CONTROLLER_GET_SCORE_LIST = "/competition/" + REQUEST_COMPETITION_ID + "/score/list";
	public static final String COMPETITION_CONTROLLER_GET_RATING = "/competition/" + REQUEST_COMPETITION_ID + "/rating";

	/*
	 * competition competitor
	 */
	public static final String COMPETITION_CONTROLLER_GET_COMPETITORS = "/" + REQUEST_COMPETITION_ID + "/competitor/all";
	public static final String COMPETITION_CONTROLLER_POST_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor";
	public static final String COMPETITION_CONTROLLER_GET_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;
	public static final String COMPETITION_CONTROLLER_GET_COMPETITOR_BY_MARK = "/" + REQUEST_COMPETITION_ID + "/mark/" + REQUEST_COMPETITOR_MARK;
	public static final String COMPETITION_CONTROLLER_PUT_COMPETITOR = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID;
	public static final String COMPETITION_CONTROLLER_POST_COMPETITOR_CHECK_MARK = "/" + REQUEST_COMPETITION_ID + "/competitor/" + REQUEST_COMPETITOR_ID + "/check";
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
	public static final String DIVISION_CONTROLLER_PUT_DIVISION = "/division/" + REQUEST_DIVISION_ID;
	public static final String DIVISION_CONTROLLER_GET_DIVISION_ROOT = "/division/root";

	/*
	 * enum
	 */
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM = "/enum";
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON = "/enum/weapon";
	public static final String COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL = "/enum/level";

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
	public static final String QUIZ_CONTROLLER_GET_SUBJECT_QUIZ = "/quiz/subject/" + REQUEST_SUBJECT_ID;
	public static final String QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE = "/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String QUIZ_CONTROLLER_POST_ANSWER_TO_QUIZ = "/report/create";
	public static final String QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION = "/quiz/" + REQUEST_QUIZ_ID + "/list";
	public static final String QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK = "/quiz/" + REQUEST_QUIZ_ID + "/list/check";
	public static final String QUIZ_CONTROLLER_GET_SUBJECTS_ENUM = "/enum/subjects";
	public static final String QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST = "/scorelist/query";
	public static final String QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE = "/scorelist/query/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;

	// checkin section
	public static final String CHECKIN_CONTROLLER = "/api/checkin";
	public static final String CHECKIN_CONTROLLER_POST_CHECK = "/check";
	public static final String CHECKIN_CONTROLLER_GET_BY_DIVISION = "/check/division/" + REQUEST_DIVISION_ID;
	public static final String CHECKIN_CONTROLLER_POST_COMBAT_NOTE = "/check/division/" + REQUEST_DIVISION_ID + "/combatnote";
	public static final String CHECKIN_CONTROLLER_GET_COMBAT_NOTE = "/check/division/" + REQUEST_DIVISION_ID + "/combatnote";
	public static final String CHECKIN_CONTROLLER_GET_INTERVAL = "/enum/interval";
	public static final String CHECKIN_CONTROLLER_GET_SEARCH_RESULT = "/search/" + REQUEST_DIVISION_ID + "/" + REQUEST_STATUS + "/" + REQUEST_INTERVAL + "/" + REQUEST_DATE;
	public static final String CHECKIN_CONTROLLER_GET_SEARCH_RESULT_BY_NAMES = "/search/" + REQUEST_DIVISION_ID + "/" + REQUEST_INTERVAL + "/" + REQUEST_DATE;
	public static final String CHECKIN_CONTROLLER_GET_LIST_COMBAT_NOTE_BY_DIVISION_BY_DATE_BY_INTERVAL = "/combatenote/" + REQUEST_DIVISION_ID + "/" + REQUEST_INTERVAL + "/" + REQUEST_DATE;

	// speciality section
	public static final String SPECIALITY_CONTROLLER = "/api/speciality";
	public static final String SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY = "/all";
	public static final String SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID = "/speciality/" + REQUEST_SPECIALITY_ID;
	public static final String SPECIALITY_CONTROLLER_POST_SPECIALITY = "/speciality";
	public static final String SPECIALITY_CONTROLLER_PUT_SPECIALITY = "/speciality/" + REQUEST_SPECIALITY_ID;
	public static final String SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID = "/speciality/" + REQUEST_SPECIALITY_ID;

	// rank section
	public static final String RANK_CONTROLLER = "/api/rank";
	public static final String RANK_CONTROLLER_GET_ALL = "/all";

	// weapon type section
	public static final String WEAPON_TYPE_CONTROLLER = "/api/weapon/type";
	public static final String WEAPON_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String WEAPON_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String WEAPON_TYPE_CONTROLLER_POST_TYPE = "/create/weapontype";
	public static final String WEAPON_TYPE_CONTROLLER_PUT_TYPE = "/update/weapontype/" + REQUEST_TYPE_ID;
	public static final String WEAPON_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/weapontype/" + REQUEST_TYPE_ID;

	// communication equipment type section
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER = "/api/commequipmenttype";
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_POST_TYPE = "/create";
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_PUT_TYPE = "/update/" + REQUEST_TYPE_ID;
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/" + REQUEST_TYPE_ID;
	public static final String COMMUNICATION_EQUIPMENT_TYPE_CONTROLLER_TYPE_ENUM = "/enum/type";

	// communication equipment type section
	public static final String EQUIPMENT_TYPE_CONTROLLER = "/api/equipmenttype";
	public static final String EQUIPMENT_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String EQUIPMENT_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String EQUIPMENT_TYPE_CONTROLLER_POST_TYPE = "/create";
	public static final String EQUIPMENT_TYPE_CONTROLLER_PUT_TYPE = "/update/" + REQUEST_TYPE_ID;
	public static final String EQUIPMENT_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/" + REQUEST_TYPE_ID;
	public static final String EQUIPMENT_TYPE_CONTROLLER_TYPE_ENUM = "/enum/type";

	// vehicle type section
	public static final String VEHICLE_TYPE_CONTROLLER = "/api/vehicletype";
	public static final String VEHICLE_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String VEHICLE_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String VEHICLE_TYPE_CONTROLLER_POST_TYPE = "/create";
	public static final String VEHICLE_TYPE_CONTROLLER_PUT_TYPE = "/update/" + REQUEST_TYPE_ID;
	public static final String VEHICLE_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/" + REQUEST_TYPE_ID;

	// ammunition type section
	public static final String AMMO_TYPE_CONTROLLER = "/api/ammo";
	public static final String AMMO_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String AMMO_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String AMMO_TYPE_CONTROLLER_POST_TYPE = "/create";
	public static final String AMMO_TYPE_CONTROLLER_PUT_TYPE = "/update/" + REQUEST_TYPE_ID;
	public static final String AMMO_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/" + REQUEST_TYPE_ID;

	// legend type section
	public static final String LEGEND_TYPE_CONTROLLER = "/api/legend";
	public static final String LEGEND_TYPE_CONTROLLER_GET_ALL = "/get/all";
	public static final String LEGEND_TYPE_CONTROLLER_GET_BY_ID = "/" + REQUEST_TYPE_ID;
	public static final String LEGEND_TYPE_CONTROLLER_POST_TYPE = "/create";
	public static final String LEGEND_TYPE_CONTROLLER_PUT_TYPE = "/update/" + REQUEST_TYPE_ID;
	public static final String LEGEND_TYPE_CONTROLLER_DELETE_TYPE_BY_ID = "/delete/" + REQUEST_TYPE_ID;

	// workspace
	public static final String WORKSPACE_CONTROLLER = "/api/workspace";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL = "/all";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST = "/allfortest";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_START = "/start";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_CHECK = "/check";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_GET_BY_CLIENT_ID = "/client/" + REQUEST_ID;
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_USE_IN_TEST = "/useintest";
	public static final String WORKSPACE_CONTROLLER_CONTROLLER_NOT_USE_IN_TEST = "/notuseintest";
	public static final String WORKSPACE_CONTROLLER_GET_TOPIC = "/get/topic";

	// weapon section
	public static final String WEAPON_CONTROLLER = "/api/weapon";
	public static final String WEAPON_CONTROLLER_GET_ALL = "/all";
	public static final String WEAPON_CONTROLLER_GET_BY_ID = "/" + REQUEST_WEAPON_ID;
	public static final String WEAPON_CONTROLLER_POST_WEAPON = "/create/weapon";
	public static final String WEAPON_CONTROLLER_PUT_WEAPON = "/edit/weapon";
	public static final String WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID = "/delete/weapon/" + REQUEST_WEAPON_ID;
	public static final String WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER = "/" + REQUEST_WEAPON_ID + "/" + REQUEST_PERSON_ID;
	public static final String WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER = "/" + REQUEST_WEAPON_ID + "/remove";
	public static final String WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT = "/" + REQUEST_WEAPON_ID + "/add/firedcount/" + REQUEST_FIRED_COUNT;
	public static final String WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID = "/all/by/division/" + REQUEST_DIVISION_ID;
	public static final String WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID = "/all/by/person/" + REQUEST_PERSON_ID;
	public static final String WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID = "/all/by/person/" + REQUEST_PERSON_NAME + "/division/" + REQUEST_DIVISION_ID;

	// communication equipment section
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER = "/api/communicationequipment";
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL = "/all";
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_GET_BY_ID = "/" + REQUEST_EQUIPMENT_ID;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_POST = "/create";
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_PUT = "/edit";
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_DELETE_BY_ID = "/delete/" + REQUEST_EQUIPMENT_ID;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_POST_ADD_OWNER = "/" + REQUEST_EQUIPMENT_ID + "/" + REQUEST_PERSON_ID;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER = "/" + REQUEST_EQUIPMENT_ID + "/remove";
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_POST_ADD_COUNT = "/" + REQUEST_EQUIPMENT_ID + "/add/firedcount/" + REQUEST_FIRED_COUNT;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID = "/all/by/division/" + REQUEST_DIVISION_ID;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID = "/all/by/person/" + REQUEST_PERSON_ID;
	public static final String COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID = "/all/by/person/" + REQUEST_PERSON_NAME + "/division/" + REQUEST_DIVISION_ID;

	// vehicle section
	public static final String VEHICLE_CONTROLLER = "/api/vehicle";
	public static final String VEHICLE_CONTROLLER_GET_ALL = "/all";
	public static final String VEHICLE_CONTROLLER_GET_BY_ID = "/" + REQUEST_VEHICLE_ID;
	public static final String VEHICLE_CONTROLLER_POST = "/create";
	public static final String VEHICLE_CONTROLLER_PUT = "/edit";
	public static final String VEHICLE_CONTROLLER_DELETE_BY_ID = "/delete/" + REQUEST_VEHICLE_ID;
	public static final String VEHICLE_CONTROLLER_POST_ADD_OWNER = "/" + REQUEST_VEHICLE_ID + "/" + REQUEST_PERSON_ID;
	public static final String VEHICLE_CONTROLLER_POST_REMOVE_OWNER = "/" + REQUEST_VEHICLE_ID + "/remove";
	public static final String VEHICLE_CONTROLLER_POST_ADD_COUNT = "/" + REQUEST_VEHICLE_ID + "/add/firedcount/" + REQUEST_FIRED_COUNT;
	public static final String VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID = "/all/by/division/" + REQUEST_DIVISION_ID;
	public static final String VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID = "/all/by/person/" + REQUEST_PERSON_ID;
	public static final String VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID = "/all/by/person/" + REQUEST_PERSON_NAME + "/division/" + REQUEST_DIVISION_ID;

	// equipment section
	public static final String EQUIPMENT_CONTROLLER = "/api/equipment";
	public static final String EQUIPMENT_CONTROLLER_GET_ALL = "/all";
	public static final String EQUIPMENT_CONTROLLER_GET_BY_ID = "/" + REQUEST_EQUIPMENT_ID;
	public static final String EQUIPMENT_CONTROLLER_POST = "/create";
	public static final String EQUIPMENT_CONTROLLER_PUT = "/edit";
	public static final String EQUIPMENT_CONTROLLER_DELETE_BY_ID = "/delete/" + REQUEST_EQUIPMENT_ID;
	public static final String EQUIPMENT_CONTROLLER_POST_ADD_OWNER = "/" + REQUEST_EQUIPMENT_ID + "/" + REQUEST_PERSON_ID;
	public static final String EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER = "/" + REQUEST_EQUIPMENT_ID + "/remove";
	public static final String EQUIPMENT_CONTROLLER_POST_ADD_COUNT = "/" + REQUEST_EQUIPMENT_ID + "/add/firedcount/" + REQUEST_FIRED_COUNT;
	public static final String EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID = "/all/by/division/" + REQUEST_DIVISION_ID;
	public static final String EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID = "/all/by/person/" + REQUEST_PERSON_ID;
	public static final String EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID = "/all/by/person/" + REQUEST_PERSON_NAME + "/division/" + REQUEST_DIVISION_ID;

	public static final String STANDARD_CONTROLLER = "/api/standard";
	public static final String STANDARD_CONTROLLER_GET_ALL = "/get/all";
	public static final String STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT = "/get/standards/" + REQUEST_SUBJECT_ID;
	public static final String STANDARD_CONTROLLER_GET_STANDARD_BY_ID = "/get/standard/" + REQUEST_STANDARD_ID;
	public static final String STANDARD_CONTROLLER_POST_STANDARD = "/post/standard";
	public static final String STANDARD_CONTROLLER_PUT_STANDARD = "/put/standard/" + REQUEST_STANDARD_ID;
	public static final String STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID = "/delete/standard/" + REQUEST_STANDARD_ID;;
	public static final String STANDARD_CONTROLLER_SCORE = "/score/" + REQUEST_STANDARD_ID;
	public static final String STANDARD_CONTROLLER_GET_SCORE = "/score/" + REQUEST_STANDARD_ID + "/" + REQUEST_PERSON_ID;
	public static final String STANDARD_CONTROLLER_GET_SCORE_LIST = "/scorelist/" + REQUEST_STANDARD_ID + "/" + REQUEST_PERSON_ID;
	public static final String STANDARD_CONTROLLER_GET_SCORE_STANDARD_LIST = "/scorelist/standard/" + REQUEST_STANDARD_ID;
	public static final String STANDARD_CONTROLLER_GET_SCORE_PERSON_LIST = "/scorelist/person/" + REQUEST_PERSON_ID;
	public static final String STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST = "/scorelist/query";
	public static final String STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE = "/scorelist/query/all/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String STANDARD_CONTROLLER_GET_PASS_ENUM = "/enum/pass";
	public static final String STANDARD_CONTROLLER_GET_UNIT_ENUM = "/enum/unit";

	public static final String STANDARD_COMMON_CONDITION_CONTROLLER = "/api/common/condition";
	public static final String STANDARD_COMMON_CONDITION_CONTROLLER_GET_ALL = "/get/all";
	public static final String STANDARD_COMMON_CONDITION_CONTROLLER_DELETE_BY_ID = "/delete/" + REQUEST_COMMON_CONDITION_ID;
	public static final String STANDARD_COMMON_CONDITION_CONTROLLER_GET_BY_ID = "/get/common/condition/" + REQUEST_COMMON_CONDITION_ID;
	public static final String STANDARD_COMMON_CONDITION_CONTROLLER_POST_CONDITION = "/post/common/condition";
	public static final String STANDARD_COMMON_CONDITION_CONTROLLER_PUT_CONDITION = "/put/common/condition/" + REQUEST_COMMON_CONDITION_ID;

	public static final String COURSE_CONTROLLER = "/api/course";
	public static final String COURSE_CONTROLLER_GET_ALL_COURSES = "/get/all/course";
	public static final String COURSE_CONTROLLER_GET_COURSE_BY_ID = "/get/course/" + REQUEST_COURSE_ID;
	public static final String COURSE_CONTROLLER_DELETE_COURSE_BY_ID = "/delete/course/" + REQUEST_COURSE_ID;
	public static final String COURSE_CONTROLLER_POST_COURSE = "/post/course/";
	public static final String COURSE_CONTROLLER_PUT_COURSE = "/put/course/" + REQUEST_COURSE_ID;
	public static final String COURSE_CONTROLLER_GET_COURSE_BY_DIVISION = "/get/by/division/" + REQUEST_DIVISION_ID + "/course";
	public static final String COURSE_CONTROLLER_GET_COURSE_BY_PERSON = "/get/by/person/" + REQUEST_PERSON_ID + "/course";
	public static final String COURSE_CONTROLLER_GET_COURCES_BY_DIVISION_BY_PAGE = "/division/" + REQUEST_DIVISION_ID + "/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String COURSE_CONTROLLER_GET_COURCES_BY_PERSON_BY_PAGE = "/person/" + REQUEST_PERSON_ID + "/" + REQUEST_PAGE_NUMBER + "/" + REQUEST_PAGE_SIZE;
	public static final String COURSE_CONTROLLER_GET_COUNT = "/count";

	public static final String SETTINGS_CONTROLLER = "/api/settings";
	public static final String SETTINGS_CONTROLLER_GET_SETTINGS = "/";
	public static final String SETTINGS_CONTROLLER_PUT_SETTINGS = "/";
	
    public static final String MAP_CONTROLLER = "/api/map";
    public static final String MAP_CONTROLLER_GET_TILE_URL = "{id}/{z}/{x}/{y}.png";
}
