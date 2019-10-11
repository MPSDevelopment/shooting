package tech.shooting.ipsc.controller;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.EquipmentTypeBean;
import tech.shooting.ipsc.enums.EquipmentTypeEnum;
import tech.shooting.ipsc.pojo.EquipmentType;
import tech.shooting.ipsc.pojo.Settings;
import tech.shooting.ipsc.service.EquipmentTypeService;
import tech.shooting.ipsc.service.SettingsService;

@Controller
@RequestMapping(ControllerAPI.SETTINGS_CONTROLLER)
@Api(ControllerAPI.SETTINGS_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {

	@Autowired
	private SettingsService service;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_GET_SETTINGS)
	@ApiOperation(value = "Return default settings")
	public ResponseEntity<Settings> getSettings() {
		return new ResponseEntity<>(service.getSettingsByName(), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SETTINGS_CONTROLLER_PUT_SETTINGS)
	@ApiOperation(value = "Return created settings", notes = "Updates default settings")
	public ResponseEntity<Settings> putSettings(@RequestBody @Valid Settings bean) {
		return new ResponseEntity<>(service.putSettings(bean), HttpStatus.OK);
	}

}
