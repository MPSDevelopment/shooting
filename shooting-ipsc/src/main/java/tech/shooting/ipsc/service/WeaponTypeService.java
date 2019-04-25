package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WeaponTypeService  {
    @Autowired
    private WeaponTypeRepository weaponRepository;

    public List<WeaponType> findAll() {
            return weaponRepository.findAll();
    }

    public Optional<WeaponType> findById(Long weaponTypeId) {
        return weaponRepository.findById(weaponTypeId);
    }

    public WeaponType save(WeaponType checkWeaponTypeExist) {
            return weaponRepository.save(checkWeaponTypeExist);
    }

    public WeaponType findByName(WeaponTypeBean bean) {
        return weaponRepository.findByName(bean);
    }

    public void removeWeaponType(WeaponType checkWeaponType) {
        weaponRepository.delete(checkWeaponType);
    }
}


