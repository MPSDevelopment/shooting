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
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.service.WeaponService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.WEAPON_CONTROLLER)
@Api(ControllerAPI.WEAPON_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class WeaponController {

    @Autowired
    private WeaponService weaponService;

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL)
    @ApiOperation(value = "Return list of weapon's", notes = "List<Weapon> or Optional.empty()")
    public ResponseEntity<List<Weapon>> getAllTypeOfWeapon() {
        return new ResponseEntity<>(weaponService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID)
    @ApiOperation(value = "Return weapon by id", notes = "Weapon or BadRequestException")
    public ResponseEntity<Weapon> getWeaponById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID)Long weaponId) throws BadRequestException {
        return new ResponseEntity<>(weaponService.getWeaponById(weaponId), HttpStatus.OK);
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_TYPE)
    @ApiOperation(value = "Return created weapon if exist update", notes = "Return created weapon if exist update")
    public ResponseEntity<Weapon> postWeapon(@RequestBody @Valid WeaponBean bean) throws BadRequestException {
        return new ResponseEntity<>(weaponService.postWeapon(bean), HttpStatus.OK);
    }

    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID)
    @ApiOperation(value = "Delete type return status Ok")
    public ResponseEntity deleteWeapon(@PathVariable (value = ControllerAPI.PATH_VARIABLE_WEAPON_ID)long weaponId) throws BadRequestException{
        weaponService.deleteWeapon(weaponId);
        return new ResponseEntity((HttpStatus.OK));
    }
}