package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.EquipmentTypeBean;
import tech.shooting.ipsc.pojo.EquipmentType;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;

@Service
public class EquipmentTypeService {

	@Autowired
	private EquipmentTypeRepository repository;

	public List<EquipmentType> getAllType() {
		List<EquipmentType> all = repository.findAll();
		return all;
	}

	public EquipmentType getTypeById(Long weaponTypeId) throws BadRequestException {
		return checkType(weaponTypeId);
	}

	private EquipmentType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect equipment type id, check id is %s", typeId)));
	}

	public EquipmentType postType(EquipmentTypeBean bean) {
		EquipmentType save = repository.save(checkTypeExist(bean));
		return save;
	}

	private EquipmentType checkTypeExist(EquipmentTypeBean bean) {
		EquipmentType byName = repository.findByName(bean);
		if (byName == null) {
			return new EquipmentType().setName(bean.getName()).setType(bean.getType());
		} else {
			return byName.setName(bean.getName()).setType(bean.getType());
		}

	}

	public void deleteType(long weaponTypeId) throws BadRequestException {
		repository.delete(checkType(weaponTypeId));
	}

	public EquipmentType updateType(long weaponTypeId, EquipmentTypeBean bean) throws BadRequestException {
		return repository.save(checkType(weaponTypeId).setName(bean.getName()).setType(bean.getType()));
	}
}
