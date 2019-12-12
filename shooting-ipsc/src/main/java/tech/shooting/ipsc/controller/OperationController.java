package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.OperationBean;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationCombatElement;
import tech.shooting.ipsc.pojo.OperationCommandantService;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationRoute;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weather;
import tech.shooting.ipsc.service.OperationService;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.OPERATION_CONTROLLER)
@Api(value = ControllerAPI.OPERATION_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class OperationController {
	
	@Autowired
	private OperationService operationService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ALL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list of all operations")
	public ResponseEntity<List<Operation>> getAll() {
		return new ResponseEntity<>(operationService.getAllOperations(), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_OPERATIONS_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get persons by page")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity<List<Operation>> getOperationsByPage(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return operationService.getOperationsByPage(page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation by id")
	public ResponseEntity<Operation> getOperationById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getOperationById(id), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get created operation")
	public ResponseEntity<Operation> postOperation(@RequestBody @Valid OperationBean bean) throws BadRequestException {
		return new ResponseEntity<>(operationService.postOperation(bean), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_PUT_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get updated operation")
	public ResponseEntity<Operation> putOperation(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid OperationBean bean) throws BadRequestException {
		return new ResponseEntity<>(operationService.putOperation(id, bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_DELETE_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete operation")
	public ResponseEntity<SuccessfulMessage> deleteOperationById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		operationService.deleteOperationById(id);
		return new ResponseEntity<>(new SuccessfulMessage("Operation was successfully deleted"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_HEADERS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation combat list headers")
	public ResponseEntity<List<OperationCombatListHeaderBean>> getOperationCombatListHeaders(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getHeaders(), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBATLIST_DATA, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation combat list data")
	public ResponseEntity<List<List<String>>> getOperationCombatListData(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getCombatListData(id, operationService.getHeaders()), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_WEATHER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set operation weather")
	public ResponseEntity<SuccessfulMessage> setWeather(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid Weather weather) throws BadRequestException {
		operationService.setWeather(id, weather);
		return new ResponseEntity<>(new SuccessfulMessage("Weather was successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_WEATHER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation weather")
	public ResponseEntity<Weather> getWeather(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getWeather(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_SYMBOLS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set operation symbols")
	public ResponseEntity<SuccessfulMessage> setSymbols(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationSymbol> symbols) throws BadRequestException {
		operationService.setSymbols(id, symbols);
		return new ResponseEntity<>(new SuccessfulMessage("Symbols were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_SYMBOLS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation symbols")
	public ResponseEntity<List<OperationSymbol>> getSymbols(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getSymbols(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_MAIN_INDICATORS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set operation main indicators")
	public ResponseEntity<SuccessfulMessage> setMainIndicators(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationMainIndicator> indicators) throws BadRequestException {
		operationService.setMainIndicatorsToOperation(id, indicators);
		return new ResponseEntity<>(new SuccessfulMessage("Main indicators were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_MAIN_INDICATORS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation main indicators")
	public ResponseEntity<List<OperationMainIndicator>> getMainIndicators(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getMainIndicators(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_PARTICIPANTS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set operation participants")
	public ResponseEntity<SuccessfulMessage> setParticipants(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<Long> participants) throws BadRequestException {
		operationService.setParticipantsToOperation(id, participants);
		return new ResponseEntity<>(new SuccessfulMessage("Participants were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_PARTICIPANTS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation participants")
	public ResponseEntity<List<OperationParticipant>> getParticioants(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getParticipants(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_SIGNALS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set operation symbols")
	public ResponseEntity<SuccessfulMessage> setCombatSignals(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationSignal> signals) throws BadRequestException {
		operationService.setCombatSignals(id, signals);
		return new ResponseEntity<>(new SuccessfulMessage("Signals were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_SIGNALS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation signals")
	public ResponseEntity<List<OperationSignal>> getSignals(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getSignals(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMMANDANT_SERVICES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set commandant services")
	public ResponseEntity<SuccessfulMessage> setCommandantServices(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationCommandantService> services) throws BadRequestException {
		operationService.setCommandantServices(id, services);
		return new ResponseEntity<>(new SuccessfulMessage("Services were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMMANDANT_SERVICES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation commandant services")
	public ResponseEntity<List<OperationCommandantService>> getCommandantServices(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getCommandantServices(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_COMBAT_ELEMENTS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set combat elements")
	public ResponseEntity<SuccessfulMessage> setCombatElements(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationCombatElement> elements) throws BadRequestException {
		operationService.setCombatElements(id, elements);
		return new ResponseEntity<>(new SuccessfulMessage("Combat elements were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_COMBAT_ELEMENTS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation elements")
	public ResponseEntity<List<OperationCombatElement>> getCombatElements(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getCombatElements(id), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_POST_ROUTES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Set combat elements")
	public ResponseEntity<SuccessfulMessage> setRoutes(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id, @RequestBody @Valid List<OperationRoute> routes) throws BadRequestException {
		operationService.setRoutes(id, routes);
		return new ResponseEntity<>(new SuccessfulMessage("Combat elements were successfully saved"), HttpStatus.OK);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.OPERATION_CONTROLLER_GET_ROUTES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get operation elements")
	public ResponseEntity<List<OperationRoute>> getRoutes(@PathVariable(value = ControllerAPI.PATH_VARIABLE_OPERATION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(operationService.getRoutes(id), HttpStatus.OK);
	}
}
