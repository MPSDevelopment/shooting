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
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.service.WeaponTypeService;

@Controller
@RequestMapping(ControllerAPI.WEAPON_TYPE_CONTROLLER)
@Api(ControllerAPI.WEAPON_TYPE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class WeaponTypeController {

    @Autowired
    private WeaponTypeService weaponService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('JUDGE')")
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_GET_ALL)
    @ApiOperation(value = "Return list of type weapon's", notes = "List<WeaponType> or Optional.empty()")
    public ResponseEntity<List<WeaponType>> getAllTypeOfWeapon() {
        return new ResponseEntity<>(weaponService.getAllType(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('JUDGE')")
    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_GET_BY_ID)
    @ApiOperation(value = "Return weapon type by id", notes = "WeaponType or BadRequestException")
    public ResponseEntity<WeaponType> getTypeOfWeaponById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_TYPE_ID)Long weaponId) throws BadRequestException {
        return new ResponseEntity<>(weaponService.getTypeById(weaponId), HttpStatus.OK);
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_POST_TYPE)
    @ApiOperation(value = "Return created weapon type", notes = "Return created name ")
    public ResponseEntity<WeaponType> postTypeOfWeapon(@RequestBody @Valid WeaponTypeBean bean) {
        return new ResponseEntity<>(weaponService.postWeaponType(bean), HttpStatus.OK);
    }

    @PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_PUT_TYPE)
    @ApiOperation(value = "Return created weapon updated", notes = "Return created name  Updated")
    public ResponseEntity<WeaponType> postTypeOfWeapon(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_TYPE_ID) long weaponTypeId,
                                                       @RequestBody @Valid WeaponTypeBean bean) throws BadRequestException {
        return new ResponseEntity<>(weaponService.updateWeaponType(weaponTypeId, bean), HttpStatus.OK);
    }

    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_TYPE_CONTROLLER_DELETE_TYPE_BY_ID)
    @ApiOperation(value = "Delete type return status Ok")
    public ResponseEntity deleteWeaponType(@PathVariable (value = ControllerAPI.PATH_VARIABLE_WEAPON_TYPE_ID)long weaponTypeId) throws BadRequestException{
        weaponService.deleteWeaponType(weaponTypeId);
        return new ResponseEntity((HttpStatus.OK));
    }

}
