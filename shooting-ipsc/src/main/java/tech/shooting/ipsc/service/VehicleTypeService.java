package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.VehicleTypeBean;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.repository.VehicleTypeRepository;

@Service
public class VehicleTypeService {

	@Autowired
	private VehicleTypeRepository repository;

	public List<VehicleType> getAllType() {
		List<VehicleType> all = repository.findAll();
		return all;
	}

	public VehicleType getTypeById(Long typeId) throws BadRequestException {
		return checkType(typeId);
	}

	private VehicleType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect weapon type id, check id is %s", typeId)));
	}

	public VehicleType postType(VehicleTypeBean bean) {
		VehicleType save = repository.save(checkTypeExist(bean));
		return save;
	}

	private VehicleType checkTypeExist(VehicleTypeBean bean) {
		VehicleType byName = repository.findByName(bean);
		if (byName == null) {
			return new VehicleType().setName(bean.getName());
		} else {
			return byName.setName(bean.getName());
		}
	}

	public void deleteType(long typeId) throws BadRequestException {
		repository.delete(checkType(typeId));
	}

	public VehicleType updateType(long typeId, VehicleTypeBean bean) throws BadRequestException {
		return repository.save(checkType(typeId).setName(bean.getName()));
	}
}
