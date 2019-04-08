package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.bean.DivisionDropList;
import tech.shooting.ipsc.controller.PageAble;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.repository.DivisionRepository;

import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
@Slf4j
public class DivisionService {
	private DivisionRepository divisionRepository;

	private MongoTemplate mongoTemplate;

	public DivisionService (DivisionRepository divisionRepository, MongoTemplate mongoTemplate) {
		this.divisionRepository = divisionRepository;
		this.mongoTemplate = mongoTemplate;
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
			division = divisionRepository.save(division);
		} else {
			BeanUtils.copyProperties(divisionBean, division, Division.PARENT_FIELD);
			Division divisionParent = divisionRepository.findById(parentId).get();
			division.setParent(divisionParent);
			division = divisionRepository.save(division);
			List<Division> children = divisionParent.getChildren();
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

	public void removeDivision (Long id) {
		divisionRepository.deleteById(id);
	}

	public Division checkDivision (Long id) throws BadRequestException {
		return divisionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division %s", id)));
	}

	public List<DivisionDropList> findAllDivisions () {
		// List<Division> all = divisionRepository.findAll();
		// List<DivisionBean> result = new ArrayList<>();
		// for(Division s : all) {
		// 	result.add(convertDivisionToFront(s));
		// }
		return mongoTemplate.aggregate(newAggregation(new MatchOperation(Criteria.where("id").exists(true))), Division.class, DivisionDropList.class).getMappedResults();

	}

	private DivisionBean convertDivisionToFront (Division division) {
		DivisionBean divisionBean = new DivisionBean();
		if(division.getParent() == null) {
			divisionBean.setName(division.getName()).setParent(null).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		} else {
			divisionBean.setName(division.getName()).setParent(division.getParent().getId()).setChildren(division.getChildren()).setActive(division.isActive()).setId(division.getId());
		}
		if(divisionBean.getChildren().contains(null) || divisionBean.getChildren().size() == 0) {
			divisionBean.setChildren(Collections.EMPTY_LIST);
		}
		return divisionBean;
	}

	public ResponseEntity getDivisionByPage (int page, int size) {
		return PageAble.getPage(page, size, divisionRepository);
	}

	public DivisionBean getDivision (Long id) throws BadRequestException {
		return convertDivisionToFront(checkDivision(id));
	}

	public DivisionBean getRoot () {
		return convertDivisionToFront(divisionRepository.findOneByParent(null));
	}

	public DivisionBean updateDivision (Long id, String name) {
		return convertDivisionToFront(divisionRepository.updateDivisionName(id, name));
	}
}
