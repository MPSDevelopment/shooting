package tech.shooting.ipsc.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CategoriesBean;
import tech.shooting.ipsc.bean.ConditionsBean;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.StandardRepository;
import tech.shooting.ipsc.repository.StandardScoreRepository;
import tech.shooting.ipsc.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StandardService {

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private StandardScoreRepository standardScoreRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	public List<Standard> getAllStandards() {
		return standardRepository.findAll();
	}

	public List<Standard> getStandardsBySubject(Long subjectId) throws BadRequestException {
		return standardRepository.findAllBySubject(checkSubject(subjectId));
	}

	private Subject checkSubject(Long subjectId) throws BadRequestException {
		return subjectRepository.findById(subjectId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect subject id %s ", subjectId)));
	}

	public Standard getStandardById(Long standardId) throws BadRequestException {
		return checkStandard(standardId);
	}

	private Standard checkStandard(Long standardId) throws BadRequestException {
		return standardRepository.findById(standardId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect standard id %s ", standardId)));
	}

	public Standard postStandard(StandardBean bean) throws BadRequestException {
		return standardRepository.save(getStandardFromBean(bean));
	}

	private Standard getStandardFromBean(StandardBean bean) throws BadRequestException {
		Subject subject = checkSubject(bean.getSubject());
		List<CategoryByTime> categories = checkCategoriesAndTime(bean.getCategoriesList());
		List<StandardConditions> conditions = checkConditionList(bean.getConditionsList());
		List<StandardFails> fails = bean.getFailsList() == null || Collections.EMPTY_LIST.equals(bean.getFailsList()) ? new ArrayList<>() : bean.getFailsList();
		return new Standard().setActive(bean.isActive()).setGroups(bean.isGroups()).setInfo(bean.getInfo()).setCategoriesList(categories).setConditionsList(conditions).setFailsList(fails).setSubject(subject);
	}

	private List<StandardConditions> checkConditionList(List<ConditionsBean> conditionsList) throws BadRequestException {
		if (conditionsList == null || Collections.EMPTY_LIST.equals(conditionsList)) {
			return new ArrayList<>();
		}
		List<StandardConditions> res = new ArrayList<>();
		for (int i = 0; i < conditionsList.size(); i++) {
			StandardConditions condition = new StandardConditions();
			UnitEnum unit = conditionsList.get(i).getUnits();
			BeanUtils.copyProperties(conditionsList.get(i), condition, StandardConditions.UNIT);
			condition.setUnits(unit);
			res.add(condition);
		}
		return res;
	}


	private List<CategoryByTime> checkCategoriesAndTime(List<CategoriesBean> categoriesList) throws BadRequestException {
		List<CategoryByTime> res = new ArrayList<>();
		for (int i = 0; i < categoriesList.size(); i++) {
			CategoriesBean bean = categoriesList.get(i);
			Category categories = bean.getCategory();
			res.add(new CategoryByTime().setCategory(categories).setExcellentTime(bean.getExcellentTime()).setGoodTime(bean.getGoodTime()).setSatisfactoryTime(bean.getSatisfactoryTime()));
		}
		return res;
	}

//	private Category checkCategory(Long categoriesBean) throws BadRequestException {
//		return categoriesRepository.findById(categoriesBean).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect category id %s", categoriesBean)));
//	}

	public Standard putStandard(Long standardId, StandardBean bean) throws BadRequestException {
		Standard standard = checkStandard(standardId);
		Standard standardFromBean = getStandardFromBean(bean);
		standard.setCategoriesList(standardFromBean.getCategoriesList()).setSubject(standardFromBean.getSubject()).setFailsList(standardFromBean.getFailsList()).setConditionsList(standardFromBean.getConditionsList())
				.setInfo(standardFromBean.getInfo()).setGroups(standardFromBean.isGroups()).setActive(standardFromBean.isActive());
		return standardRepository.save(standard);
	}

	public void deleteStandardById(Long standardId) {
		standardRepository.deleteById(standardId);
	}

	public StandardScore addScore(Long standardId, StandardScore score) {
		return standardScoreRepository.save(score);
	}

	public StandardScore getScore(Long standardId, Long personId) {
		var list = standardScoreRepository.findByPersonIdAndStandardId(personId, standardId);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}
	
	public List<StandardScore> getScoreList(Long standardId, Long personId) {
		return standardScoreRepository.findByPersonIdAndStandardId(personId, standardId);
	}
	
	public List<StandardScore> getScoreStandardList(Long standardId) {
		return standardScoreRepository.findAllByStandardId(standardId);
	}
	
	public List<StandardScore> getScorePersonList(Long personId) {
		return standardScoreRepository.findAllByPersonId(personId);
	}
}
