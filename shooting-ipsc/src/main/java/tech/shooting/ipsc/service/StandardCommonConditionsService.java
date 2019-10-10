package tech.shooting.ipsc.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.StandardCommonConditionsBean;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.StandardCommonConditions;
import tech.shooting.ipsc.repository.CommonConditionsRepository;

import java.util.List;

@Service
public class StandardCommonConditionsService {

	@Autowired
	private CommonConditionsRepository conditionsRepository;

	public List<StandardCommonConditions> getAllCommonConditions() {
		return conditionsRepository.findAll();
	}

	public void deleteConditionById(Long commonConditionId) throws BadRequestException {
		conditionsRepository.delete(checkCommonCondition(commonConditionId));
	}

	private StandardCommonConditions checkCommonCondition(Long commonConditionId) throws BadRequestException {
		return conditionsRepository.findById(commonConditionId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect commonCondition id %s", commonConditionId)));
	}

	public StandardCommonConditions getConditionById(Long commonConditionId) throws BadRequestException {
		return checkCommonCondition(commonConditionId);
	}

	public StandardCommonConditions postCommonCondition(StandardCommonConditionsBean bean) throws BadRequestException {
		UnitEnum units = bean.getUnits();
		StandardCommonConditions conditions = new StandardCommonConditions();
		BeanUtils.copyProperties(bean, conditions, StandardCommonConditions.COMMON_CONDITION_UNITS);
		conditions.setUnits(units);
		return conditionsRepository.save(conditions);
	}

	public StandardCommonConditions putCommonCondition(Long commonConditionId, StandardCommonConditionsBean bean) throws BadRequestException {
		StandardCommonConditions conditions = checkCommonCondition(commonConditionId);
		UnitEnum units = bean.getUnits();
		BeanUtils.copyProperties(bean, conditions, StandardCommonConditions.COMMON_CONDITION_UNITS);
		conditions.setUnits(units);
		return conditionsRepository.save(conditions);
	}
}
