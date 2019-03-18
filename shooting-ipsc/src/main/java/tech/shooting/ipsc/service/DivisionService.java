package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.repository.DivisionRepository;

@Service
@Slf4j
public class DivisionService {
	@Autowired
	private DivisionRepository divisionRepository;

	public DivisionService (DivisionRepository divisionRepository) {
		this.divisionRepository = divisionRepository;
	}

	public Division createDivision (DivisionBean divisionBean, Long parentId) {
		if(divisionRepository.findByNameAndParent(divisionBean.getName(), parentId) != null) {
			throw new ValidationException(Division.NAME_WITH_PARENT + "Division with name %s and parent id %s is already exist", divisionBean.getName(), parentId);
		}
		Division division = new Division();
		BeanUtils.copyProperties(divisionBean, division);
		return divisionRepository.save(division);
	}
}
