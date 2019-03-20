package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.controller.PageAble;
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
		Division division = createDivisionWithCheck(divisionBean, parentId);
		return convertDivisionToFront(division);
	}

	private Division createDivisionWithCheck (DivisionBean divisionBean, Long parentId) {
		Division division = new Division();
		if(parentId == null) {
			BeanUtils.copyProperties(divisionBean, division);
			if(division.getChildren() == null) {
				division.setChildren(new ArrayList<>());
			}
			division = divisionRepository.save(division);
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
		}
		return division;
	}

	public void deleteAllDivision () {
		divisionRepository.deleteAll();
	}

	public int getCount () {
		return divisionRepository.findAll().size();
	}

	public void removeDivision (Long id) throws BadRequestException {
		Division division = checkDivision(id);
		divisionRepository.delete(division);
	}

	public Division checkDivision (Long id) throws BadRequestException {
		return divisionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division %s", id)));
	}

	public List<DivisionBean> findAllDivisions () {
		List<Division> all = divisionRepository.findAll();
		List<DivisionBean> result = new ArrayList<>();
		for(Division s : all) {
			result.add(convertDivisionToFront(s));
		}
		return result;
	}

	private DivisionBean convertDivisionToFront (Division division) {
		DivisionBean divisionBean = new DivisionBean();
		if(division.getParent() == null) {
			divisionBean.setName(division.getName()).setParent(null).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		} else {
			divisionBean.setName(division.getName()).setParent(division.getParent().getId()).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		}
		return divisionBean;
	}

	public ResponseEntity getDivisionByPage (int page, int size) {
		return PageAble.getPage(page, size, divisionRepository);
	}

	public DivisionBean getDivision (Long id) throws BadRequestException {
		return convertDivisionToFront(checkDivision(id));
	}

	public DivisionBean updateDivision (DivisionBean divisionBean, Long id) throws BadRequestException {
		Division division = checkDivision(id);
		BeanUtils.copyProperties(divisionBean, division);
		division = divisionRepository.save(division);
		return convertDivisionToFront(division);
	}
}
