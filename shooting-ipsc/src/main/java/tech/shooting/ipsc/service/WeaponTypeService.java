package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

import java.util.List;

@Service
public class WeaponTypeService  {
    @Autowired
    private WeaponTypeRepository weaponType;

    public List<WeaponType> getAllType() {
        List<WeaponType> all = weaponType.findAll();
        return all;
    }

    public WeaponType getTypeById(Long weaponTypeId) throws BadRequestException {
        return checkWeaponType(weaponTypeId);
    }

    private WeaponType checkWeaponType(Long weaponTypeId) throws BadRequestException {
        return weaponType.findById(weaponTypeId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect weapon type id, check id is %s",weaponTypeId)));
    }

    public WeaponType postWeaponType(WeaponTypeBean bean) {
        WeaponType save = weaponType.save(checkWeaponTypeExist(bean));
        return save;
    }

    private WeaponType checkWeaponTypeExist(WeaponTypeBean bean) {
        WeaponType byName = weaponType.findByName(bean);
        if (byName==null){
            return new WeaponType().setName(bean.getName());
        }else{
            return byName.setName(bean.getName());
        }

    }

    public void deleteWeaponType(long weaponTypeId) throws BadRequestException {
        weaponType.delete(checkWeaponType(weaponTypeId));
    }
}


