package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.repository.DivisionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DivisionService {
	private DivisionRepository divisionRepository;

	public DivisionService (DivisionRepository divisionRepository) {
		this.divisionRepository = divisionRepository;
	}

	public DivisionBean createDivision (DivisionBean divisionBean, Long parentId) {
		if(divisionRepository.findByNameAndParent(divisionBean.getName(), parentId) != null) {
			throw new ValidationException(Division.NAME_WITH_PARENT + "Division with name %s and parent id %s is already exist", divisionBean.getName(), parentId);
		}
		Division division = new Division();
		DivisionBean divisionBeanToFront = new DivisionBean();
		if(parentId == null) {
			BeanUtils.copyProperties(divisionBean, division);
			division = divisionRepository.save(division);
			divisionBean.setName(division.getName()).setParent(null).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		} else {
			BeanUtils.copyProperties(divisionBean, division, Division.PARENT_FIELD);
			Division divisionParent = divisionRepository.findById(parentId).get();
			division.setParent(divisionParent);
			division = divisionRepository.save(division);
			List<Division> children = divisionParent.getChildren();
			if(children == null) {
				children = new ArrayList<>();
			}
			children.add(division);
			divisionParent.setChildren(children);
			divisionRepository.save(divisionParent);
			divisionBean.setName(division.getName()).setParent(divisionParent.getId()).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		}
		return divisionBean;
	}

	public void deleteAllDivision () {
		divisionRepository.deleteAll();
	}

	public int getCount () {
		return divisionRepository.findAll().size();
	}
}
