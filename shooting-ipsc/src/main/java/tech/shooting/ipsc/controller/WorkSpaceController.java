package tech.shooting.ipsc.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.WorkSpaceBean;
import tech.shooting.ipsc.service.WorkSpaceService;

@Controller
@RequestMapping(ControllerAPI.WORKSPACE_CONTROLLER)
@Api(ControllerAPI.WORKSPACE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('GUEST')")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_POST_WORKSPACE)
    @ApiOperation(value = "Define work space")
    public ResponseEntity postNewWorkSpace(HttpServletRequest request) {
        log.info("Connection from -> Remote  X-Forwarded-For = %s  ipAddress =  %s ", request.getHeader("X-Forwarded-For"), request.getRemoteAddr());
        String remoteIp = request.getHeader("X-Forwarded-For");
        workSpaceService.createWorkSpace(remoteIp);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_GET_TOPIC)
    @ApiOperation(value = "Create topic for workspace publisher")
    public ResponseEntity getPublisherTopic() {
        workSpaceService.createTopicForAdmin();
        return new ResponseEntity(new SuccessfulMessage("Guest is success added to list work space"), HttpStatus.OK);
    }

    @PreAuthorize(" hasRole('USER') or hasRole('ADMIN')")
    @PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WORKSPACE_CONTROLLER_GET_TOPIC)
    @ApiOperation(value = "Start test to workspace")
    public ResponseEntity getPublisherTopic(@RequestBody List<WorkSpaceBean> beans) throws BadRequestException {
        workSpaceService.updateWorkSpaceDataAndStartTest(beans);
        return new ResponseEntity(new SuccessfulMessage("Admin update work space and start test"), HttpStatus.OK);
    }
}
