package tech.shooting.ipsc.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.bean.WorkSpaceBean;
import tech.shooting.ipsc.pojo.Workspace;
import tech.shooting.ipsc.service.WorkspaceService;

@Controller
@RequestMapping(ControllerAPI.WORKSPACE_CONTROLLER)
@Api(ControllerAPI.WORKSPACE_CONTROLLER)
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class WorkspaceController {

	@Autowired
	private WorkspaceService workSpaceService;

	@PutMapping(value = ControllerAPI.VERSION_1_0)
	@ApiOperation(value = "Set workspace")
	public ResponseEntity<Workspace> putWorkspace(@RequestBody @Valid WorkSpaceBean bean) throws BadRequestException {
		Workspace workspace = workSpaceService.updateWorkspace(bean);
		return new ResponseEntity<>(workspace, HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_START)
	@ApiOperation(value = "Start workspace test")
	public ResponseEntity<Workspace> startWorkspace(@RequestBody @Valid WorkSpaceBean bean) throws BadRequestException, MqttPersistenceException, MqttException {
		Workspace workspace = workSpaceService.startWorkspace(bean);
		return new ResponseEntity<>(workspace, HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0)
	@ApiOperation(value = "Get a workspace")
	public ResponseEntity<Workspace> getWorkspace(HttpServletRequest request) throws BadRequestException, NotFoundException {

		log.info("Connection from -> Remote  X-Forwarded-For = %s  ipAddress =  %s ", request.getHeader("X-Forwarded-For"), request.getRemoteAddr());
		String remoteIp = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse(request.getRemoteAddr());

		Workspace workspace = workSpaceService.getWorkspaceByIp(remoteIp);
		return new ResponseEntity<>(workspace, HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Get all workspaces")
	public ResponseEntity<Collection<Workspace>> getAll() throws MqttException {
		return new ResponseEntity<>(workSpaceService.getAllWorkspaces(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_CONTROLLER_GET_ALL_FOR_TEST)
	@ApiOperation(value = "Get all workspaces for test")
	public ResponseEntity<Collection<Workspace>> getAllForTest() throws MqttException {
		return new ResponseEntity<>(workSpaceService.getAllWorkspacesForTest(), HttpStatus.OK);
	}

//    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
//    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_GET_TOPIC)
//    @ApiOperation(value = "Create topic for workspace publisher")
//    public ResponseEntity<SuccessfulMessage> getPublisherTopic() {
//        workSpaceService.createTopicForAdmin();
//        return new ResponseEntity<>(new SuccessfulMessage("Guest is success added to list work space"), HttpStatus.OK);
//    }

//    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
//    @PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_GET_TOPIC)
//    @ApiOperation(value = "Start test to workspace")
//    public ResponseEntity<SuccessfulMessage> getPublisherTopic(@RequestBody List<WorkSpaceBean> beans) throws BadRequestException {
//        workSpaceService.updateWorkSpaceDataAndStartTest(beans);
//        return new ResponseEntity<>(new SuccessfulMessage("Admin update work space and start test"), HttpStatus.OK);
//    }
}
