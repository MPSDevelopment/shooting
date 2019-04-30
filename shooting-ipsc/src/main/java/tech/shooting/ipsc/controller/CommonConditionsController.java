package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.CommonConditionsBean;
import tech.shooting.ipsc.pojo.CommonConditions;
import tech.shooting.ipsc.service.CommonConditionsService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.COMMON_CONDITION_CONTROLLER)
@Api(value = ControllerAPI.COMMON_CONDITION_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class CommonConditionsController {
    @Autowired
    private CommonConditionsService conditionsService;


    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMON_CONDITION_CONTROLLER_GET_ALL)
    @ApiOperation(value = "Get list all common conditions")
    public ResponseEntity<List<CommonConditions>> getAllCommonConditions() {
        return new ResponseEntity<>(conditionsService.getAllCommonConditions(), HttpStatus.OK);
    }

    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMON_CONDITION_CONTROLLER_DELETE_BY_ID)
    @ApiOperation(value = "Get status is ok, when row are removed")
    public ResponseEntity deleteCommonConditionById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMMON_CONDITION_ID)Long commonConditionId) throws BadRequestException {
        conditionsService.deleteConditionById(commonConditionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMON_CONDITION_CONTROLLER_GET_BY_ID)
    @ApiOperation(value = "Get common condition by id")
    public ResponseEntity<CommonConditions> getCommonConditionById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMMON_CONDITION_ID)Long commonConditionId) throws BadRequestException {
        return new ResponseEntity<>(conditionsService.getConditionById(commonConditionId),HttpStatus.OK);
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMON_CONDITION_CONTROLLER_POST_CONDITION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get common condition")
    public ResponseEntity<CommonConditions> postCommonCondition(@RequestBody @Valid CommonConditionsBean bean) throws BadRequestException {
        return new ResponseEntity<>(conditionsService.postCommonCondition(bean),HttpStatus.CREATED);
    }

    @PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMON_CONDITION_CONTROLLER_PUT_CONDITION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get updated common condition")
    public ResponseEntity<CommonConditions> putCommonCondition(@PathVariable (value = ControllerAPI.PATH_VARIABLE_COMMON_CONDITION_ID)Long commonConditionId,@RequestBody @Valid CommonConditionsBean bean) throws BadRequestException {
        return new ResponseEntity<>(conditionsService.putCommonCondition(commonConditionId,bean),HttpStatus.OK);
    }
}
