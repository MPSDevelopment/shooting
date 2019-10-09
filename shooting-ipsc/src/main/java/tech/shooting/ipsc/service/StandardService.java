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
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.CategoriesRepository;
import tech.shooting.ipsc.repository.StandardRepository;
import tech.shooting.ipsc.repository.StandardScoreRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.UnitsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

@Service
public class StandardService {

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private StandardScoreRepository standardScoreRepository;

	@Autowired
	private UnitsRepository unitsRepository;

	@Autowired
	private CategoriesRepository categoriesRepository;

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
		List<CategoriesAndTime> categories = checkCategoriesAndTime(bean.getCategoriesList());
		List<Conditions> conditions = checkConditionList(bean.getConditionsList());
		List<Fails> fails = bean.getFailsList() == null || Collections.EMPTY_LIST.equals(bean.getFailsList()) ? new ArrayList<>() : bean.getFailsList();
		return new Standard().setActive(bean.isActive()).setGroups(bean.isGroups()).setInfo(bean.getInfo()).setCategoriesList(categories).setConditionsList(conditions).setFailsList(fails).setSubject(subject);
	}

	private List<Conditions> checkConditionList(List<ConditionsBean> conditionsList) throws BadRequestException {
		if (conditionsList == null || Collections.EMPTY_LIST.equals(conditionsList)) {
			return new ArrayList<>();
		}
		List<Conditions> res = new ArrayList<>();
		for (int i = 0; i < conditionsList.size(); i++) {
			Conditions condition = new Conditions();
			Units unit = checkUnit(conditionsList.get(i).getUnits());
			BeanUtils.copyProperties(conditionsList.get(i), condition, Conditions.UNIT);
			condition.setUnits(unit);
			res.add(condition);
		}
		return res;
	}

	private Units checkUnit(Long units) throws BadRequestException {
		return unitsRepository.findById(units).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect unit id %s", units)));
	}

	private List<CategoriesAndTime> checkCategoriesAndTime(List<CategoriesBean> categoriesList) throws BadRequestException {
		List<CategoriesAndTime> res = new ArrayList<>();
		for (int i = 0; i < categoriesList.size(); i++) {
			CategoriesBean bean = categoriesList.get(i);
			Categories categories = checkCategory(bean.getCategory());
			res.add(new CategoriesAndTime().setCategory(categories).setExcellentTime(bean.getExcellentTime()).setGoodTime(bean.getGoodTime()).setSatisfactoryTime(bean.getSatisfactoryTime()));
		}
		return res;
	}

	private Categories checkCategory(Long categoriesBean) throws BadRequestException {
		return categoriesRepository.findById(categoriesBean).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect category id %s", categoriesBean)));
	}

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
}
