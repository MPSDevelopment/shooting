package tech.shooting.ipsc.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.ConditionsBean;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.bean.StandardScoreBean;
import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.event.TagImitatorEvent;
import tech.shooting.ipsc.event.TagImitatorOnlyCodesEvent;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.StandardRepository;
import tech.shooting.ipsc.repository.StandardScoreRepository;
import tech.shooting.ipsc.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardService {

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private StandardScoreRepository standardScoreRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private PersonRepository personRepository;

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

	private Person checkPerson(Long id) throws BadRequestException {
		return personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s ", id)));
	}

	public Standard postStandard(StandardBean bean) throws BadRequestException {
		return standardRepository.save(getStandardFromBean(bean));
	}

	private Standard getStandardFromBean(StandardBean bean) throws BadRequestException {
		Subject subject = checkSubject(bean.getSubject());
		List<StandardConditions> conditions = checkConditionList(bean.getConditionsList());
		List<StandardFails> fails = bean.getFailsList() == null || Collections.EMPTY_LIST.equals(bean.getFailsList()) ? new ArrayList<>() : bean.getFailsList();
		return new Standard().setActive(bean.isActive()).setGroups(bean.isGroups()).setRunning(bean.isRunning()).setInfo(bean.getInfo()).setCategoryByTimeList(bean.getCategoryByTimeList())
				.setCategoryByPointsList(bean.getCategoryByPointsList()).setConditionsList(conditions).setFailsList(fails).setSubject(subject).setLaps(bean.getLaps());
	}

	private List<StandardConditions> checkConditionList(List<ConditionsBean> conditionsList) throws BadRequestException {
		if (CollectionUtils.isEmpty(conditionsList)) {
			return new ArrayList<>();
		}
		List<StandardConditions> res = new ArrayList<>();
		for (int i = 0; i < conditionsList.size(); i++) {
			StandardConditions condition = new StandardConditions();
			BeanUtils.copyProperties(conditionsList.get(i), condition);
			res.add(condition);
		}
		return res;
	}

//	private List<CategoryByTime> checkCategoriesAndTime(List<CategoryByTime> categoriesList) throws BadRequestException {
//		List<CategoryByTime> res = new ArrayList<>();
//		for (int i = 0; i < categoriesList.size(); i++) {
//			CategoryByTime bean = categoriesList.get(i);
//			Category categories = bean.getCategory();
//			res.add(new CategoryByTime().setCategory(categories).setExcellentTime(bean.getExcellentTime()).setGoodTime(bean.getGoodTime()).setSatisfactoryTime(bean.getSatisfactoryTime()));
//		}
//		return res;
//	}

//	private Category checkCategory(Long categoriesBean) throws BadRequestException {
//		return categoriesRepository.findById(categoriesBean).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect category id %s", categoriesBean)));
//	}

	public Standard putStandard(Long standardId, StandardBean bean) throws BadRequestException {
		Standard standard = checkStandard(standardId);
		Standard standardFromBean = getStandardFromBean(bean);

		standard.setCategoryByTimeList(standardFromBean.getCategoryByTimeList()).setCategoryByPointsList(standardFromBean.getCategoryByPointsList()).setSubject(standardFromBean.getSubject()).setFailsList(standardFromBean.getFailsList())
				.setConditionsList(standardFromBean.getConditionsList()).setInfo(standardFromBean.getInfo()).setGroups(standardFromBean.isGroups()).setRunning(standardFromBean.isRunning()).setLaps(bean.getLaps())
				.setActive(standardFromBean.isActive());
		return standardRepository.save(standard);
	}

	public void deleteStandardById(Long standardId) {
		standardRepository.deleteById(standardId);
	}

	public StandardScore addScore(Long standardId, StandardScoreBean bean) throws BadRequestException {
		var person = checkPerson(bean.getPersonId());
		var standard = checkStandard(bean.getStandardId());
		var score = new StandardScore();
		BeanUtils.copyProperties(bean, score, StandardScore.PERSON_FIELD);
		score.setPerson(person);
		score.setStandardInfo(standard.getInfo());
		return standardScoreRepository.save(score);
	}

	public StandardScore getScore(Long standardId, Long personId) {
		var list = standardScoreRepository.findByPersonIdAndStandardId(personId, standardId);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	public List<StandardScore> getScoreList(Long standardId, Long personId) {
		return addStandardInfo(standardScoreRepository.findByPersonIdAndStandardId(personId, standardId));
	}

	public List<StandardScore> getScoreStandardList(Long standardId) {
		return addStandardInfo(standardScoreRepository.findAllByStandardId(standardId));
	}

	public List<StandardScore> getScorePersonList(Long personId) {
		return addStandardInfo(standardScoreRepository.findAllByPersonId(personId));
	}

	public List<StandardScore> getScoreList(StandardScoreRequest query) throws BadRequestException {
		if (query.getStandardId() != null) {
			checkStandard(query.getStandardId());
		}
		if (query.getSubjectId() != null) {
			checkSubject(query.getSubjectId());
		}
		if (query.getPersonId() != null) {
			checkPerson(query.getPersonId());
		}

		return standardScoreRepository.getScoreList(query);
	}

	public ResponseEntity<List<StandardScore>> getScoreList(StandardScoreRequest query, Integer page, Integer size) {
		page = Math.max(1, page);
		page--;
		size = Math.min(Math.max(10, size), 20);
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, QuizScore.TIME_FIELD);
		var list = standardScoreRepository.getScoreList(query, pageable);
		return new ResponseEntity<>(list.getContent(), Pageable.setHeaders(page, list.getTotalElements(), list.getTotalPages()), HttpStatus.OK);
	}

	// TODO Remove it after we have normally stored scores
	public List<StandardScore> addStandardInfo(List<StandardScore> scores) {
		scores.forEach(item -> {
			if (item.getStandardInfo() == null) {
				try {
					item.setStandardInfo(checkStandard(item.getStandardId()).getInfo());
				} catch (BadRequestException e) {

				}
			}
		});
		return scores;

	}

	public void startImitator(Long standardId) throws BadRequestException {
		Standard standard = checkStandard(standardId);
		EventBus.publishEventAsync(new TagImitatorEvent(standardId, standard.getLaps() == null ? 5 : standard.getLaps(), personRepository.findAll()));
	}

	public void startImitatorOnlyCodes(Long standardId) throws BadRequestException {
		Standard standard = checkStandard(standardId);
		EventBus.publishEventAsync(new TagImitatorOnlyCodesEvent(standardId, standard.getLaps() == null ? 5 : standard.getLaps(), personRepository.findAll()));
	}
}
