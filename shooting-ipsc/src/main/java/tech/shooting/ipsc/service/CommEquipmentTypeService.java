package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CommEquipmentTypeBean;
import tech.shooting.ipsc.pojo.CommEquipmentType;
import tech.shooting.ipsc.repository.CommEquipmentTypeRepository;

@Service
public class CommEquipmentTypeService {

	@Autowired
	private CommEquipmentTypeRepository repository;

	public List<CommEquipmentType> getAllType() {
		List<CommEquipmentType> all = repository.findAll();
		return all;
	}

	public CommEquipmentType getTypeById(Long weaponTypeId) throws BadRequestException {
		return checkType(weaponTypeId);
	}

	private CommEquipmentType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect communication equipment type id, check id is %s", typeId)));
	}

	public CommEquipmentType postType(CommEquipmentTypeBean bean) {
		CommEquipmentType save = repository.save(checkWeaponTypeExist(bean));
		return save;
	}

	private CommEquipmentType checkWeaponTypeExist(CommEquipmentTypeBean bean) {
		CommEquipmentType byName = repository.findByName(bean);
		if (byName == null) {
			return new CommEquipmentType().setName(bean.getName());
		} else {
			return byName.setName(bean.getName());
		}

	}

	public void deleteType(long weaponTypeId) throws BadRequestException {
		repository.delete(checkType(weaponTypeId));
	}

	public CommEquipmentType updateType(long weaponTypeId, CommEquipmentTypeBean bean) throws BadRequestException {
		return repository.save(checkType(weaponTypeId).setName(bean.getName()));
	}
}
