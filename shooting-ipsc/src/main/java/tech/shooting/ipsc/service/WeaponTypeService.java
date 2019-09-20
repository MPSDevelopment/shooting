package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class WeaponTypeService  {
	
    @Autowired
    private WeaponTypeRepository repository;

    public List<WeaponType> getAllType() {
        List<WeaponType> all = repository.findAll();
        return all;
    }

    public WeaponType getTypeById(Long weaponTypeId) throws BadRequestException {
        return checkType(weaponTypeId);
    }

    private WeaponType checkType(Long weaponTypeId) throws BadRequestException {
        return repository.findById(weaponTypeId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect weapon type id, check id is %s",weaponTypeId)));
    }

    public WeaponType postType(WeaponTypeBean bean) {
        WeaponType save = repository.save(checkWeaponTypeExist(bean));
        return save;
    }

    private WeaponType checkWeaponTypeExist(WeaponTypeBean bean) {
        WeaponType byName = repository.findByName(bean.getName());
        if (byName==null){
            return new WeaponType().setName(bean.getName()).setAmmoCount(bean.getAmmoCount());
        }else{
            return byName.setName(bean.getName()).setAmmoCount(bean.getAmmoCount());
        }

    }

    public void deleteType(long typeId) throws BadRequestException {
        repository.delete(checkType(typeId));
    }

    public WeaponType updateType(long typeId, WeaponTypeBean bean) throws BadRequestException {
        return repository.save(checkType(typeId).setName(bean.getName()));
    }
}


