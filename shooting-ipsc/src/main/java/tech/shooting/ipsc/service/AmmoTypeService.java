package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.AmmoTypeBean;
import tech.shooting.ipsc.pojo.AmmoType;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.AmmoTypeRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class AmmoTypeService {

	@Autowired
	private AmmoTypeRepository repository;
	
	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	public List<AmmoType> getAllType() {
		List<AmmoType> all = repository.findAll();
		return all;
	}

	public AmmoType getTypeById(Long typeId) throws BadRequestException {
		return checkType(typeId);
	}

	private AmmoType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect ammunition type id, check id is %s", typeId)));
	}
	
	private WeaponType checkWeaponType(Long typeId) throws BadRequestException {
		return weaponTypeRepository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect weapon type id, check id is %s", typeId)));
	}

	public AmmoType postType(AmmoTypeBean bean) throws BadRequestException {
		AmmoType save = repository.save(checkTypeExist(bean));
		return save;
	}

	private AmmoType checkTypeExist(AmmoTypeBean bean) throws BadRequestException {
		AmmoType byName = repository.findByName(bean.getName());
		if (byName == null) {
			return new AmmoType().setName(bean.getName()).setAmmunitionWeaponType(checkWeaponType(bean.getAmmunitionWeaponTypeId())).setCount(bean.getCount());
		} else {
			return byName.setName(bean.getName());
		}
	}

	public void deleteType(long typeId) throws BadRequestException {
		repository.delete(checkType(typeId));
	}

	public AmmoType updateType(long typeId, AmmoTypeBean bean) throws BadRequestException {
		return repository.save(checkType(typeId).setName(bean.getName()).setAmmunitionWeaponType(checkWeaponType(bean.getAmmunitionWeaponTypeId())).setCount(bean.getCount()));
	}
}
