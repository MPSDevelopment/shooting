package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CommunicationEquipmentTypeBean;
import tech.shooting.ipsc.pojo.CommunicationEquipmentType;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;

@Service
public class CommunicationEquipmentTypeService {

	@Autowired
	private CommunicationEquipmentTypeRepository repository;

	public List<CommunicationEquipmentType> getAllType() {
		List<CommunicationEquipmentType> all = repository.findAll();
		return all;
	}

	public CommunicationEquipmentType getTypeById(Long weaponTypeId) throws BadRequestException {
		return checkType(weaponTypeId);
	}

	private CommunicationEquipmentType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect communication equipment type id, check id is %s", typeId)));
	}

	public CommunicationEquipmentType postType(CommunicationEquipmentTypeBean bean) {
		CommunicationEquipmentType save = repository.save(checkWeaponTypeExist(bean));
		return save;
	}

	private CommunicationEquipmentType checkWeaponTypeExist(CommunicationEquipmentTypeBean bean) {
		CommunicationEquipmentType byName = repository.findByName(bean);
		if (byName == null) {
			return new CommunicationEquipmentType().setName(bean.getName()).setType(bean.getType());
		} else {
			return byName.setName(bean.getName()).setType(bean.getType());
		}

	}

	public void deleteType(long weaponTypeId) throws BadRequestException {
		repository.delete(checkType(weaponTypeId));
	}

	public CommunicationEquipmentType updateType(long weaponTypeId, CommunicationEquipmentTypeBean bean) throws BadRequestException {
		return repository.save(checkType(weaponTypeId).setName(bean.getName()));
	}
}
