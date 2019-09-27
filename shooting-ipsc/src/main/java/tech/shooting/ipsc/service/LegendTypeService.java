package tech.shooting.ipsc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.AmmoTypeBean;
import tech.shooting.ipsc.bean.LegendTypeBean;
import tech.shooting.ipsc.pojo.AmmoType;
import tech.shooting.ipsc.pojo.LegendType;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.AmmoTypeRepository;
import tech.shooting.ipsc.repository.LegendTypeRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class LegendTypeService {

	@Autowired
	private LegendTypeRepository repository;

	public List<LegendType> getAllType() {
		List<LegendType> all = repository.findAll();
		return all;
	}

	public LegendType getTypeById(Long typeId) throws BadRequestException {
		return checkType(typeId);
	}

	private LegendType checkType(Long typeId) throws BadRequestException {
		return repository.findById(typeId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect legend type id, check id is %s", typeId)));
	}

	public LegendType postType(LegendTypeBean bean) throws BadRequestException {
		LegendType save = repository.save(checkTypeExist(bean));
		return save;
	}

	private LegendType checkTypeExist(LegendTypeBean bean) throws BadRequestException {
		LegendType byName = repository.findByName(bean.getName());
		if (byName == null) {
			return new LegendType().setName(bean.getName()).setType(bean.getType());
		} else {
			return byName.setName(bean.getName()).setType(bean.getType());
		}
	}

	public void deleteType(long typeId) throws BadRequestException {
		repository.delete(checkType(typeId));
	}

	public LegendType updateType(long typeId, LegendTypeBean bean) throws BadRequestException {
		return repository.save(checkType(typeId).setName(bean.getName())).setType(bean.getType());
	}
}
