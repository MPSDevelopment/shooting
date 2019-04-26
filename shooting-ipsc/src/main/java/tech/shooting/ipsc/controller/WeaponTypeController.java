package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.service.WeaponTypeService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.WEAPON_TYPE_CONTROLLER)
@Api(ControllerAPI.WEAPON_TYPE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class WeaponTypeController {

    @Autowired
    private WeaponTypeService weaponService;

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_GET_ALL)
    @ApiOperation(value = "Return list of type weapon's", notes = "List<WeaponType> or Optional.empty()")
    public ResponseEntity<List<WeaponType>> getAllTypeOfWeapon() {
        return new ResponseEntity<>(weaponService.getAllType(), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_GET_BY_ID)
    @ApiOperation(value = "Return weapon type by id", notes = "WeaponType or BadRequestException")
    public ResponseEntity<WeaponType> getTypeOfWeaponById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_TYPE_ID)Long weaponId) throws BadRequestException {
        return new ResponseEntity<>(weaponService.getTypeById(weaponId), HttpStatus.OK);
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_POST_TYPE)
    @ApiOperation(value = "Return created weapon type or updated", notes = "Return created weaponType or Updated")
    public ResponseEntity<WeaponType> postTypeOfWeapon(@RequestBody @Valid WeaponTypeBean bean) {
        return new ResponseEntity<>(weaponService.postWeaponType(bean), HttpStatus.OK);
    }

    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_DELETE_TYPE_BY_ID)
    @ApiOperation(value = "Delete type return status Ok")
    public ResponseEntity deleteWeaponType(@PathVariable (value = ControllerAPI.PATH_VARIABLE_WEAPON_TYPE_ID)long weaponTypeId) throws BadRequestException{
        weaponService.deleteWeaponType(weaponTypeId);
        return new ResponseEntity((HttpStatus.OK));
    }

}
