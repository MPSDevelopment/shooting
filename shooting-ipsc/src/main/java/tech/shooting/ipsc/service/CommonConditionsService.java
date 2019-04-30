package tech.shooting.ipsc.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CommonConditionsBean;
import tech.shooting.ipsc.pojo.CommonConditions;
import tech.shooting.ipsc.pojo.Units;
import tech.shooting.ipsc.repository.CommonConditionsRepository;
import tech.shooting.ipsc.repository.UnitsRepository;

import java.util.List;

@Service
public class CommonConditionsService {

    @Autowired
    private CommonConditionsRepository conditionsRepository;

    @Autowired
    private UnitsRepository unitsRepository;

    public List<CommonConditions> getAllCommonConditions() {
        return conditionsRepository.findAll();
    }

    public void deleteConditionById(Long commonConditionId) throws BadRequestException {
        conditionsRepository.delete(checkCommonCondition(commonConditionId));
    }

    private CommonConditions checkCommonCondition(Long commonConditionId) throws BadRequestException {
        return conditionsRepository.findById(commonConditionId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect commonCondition id %s", commonConditionId)));
    }

    public CommonConditions getConditionById(Long commonConditionId) throws BadRequestException {
        return checkCommonCondition(commonConditionId);
    }

    public CommonConditions postCommonCondition(CommonConditionsBean bean) throws BadRequestException {
        Units units = checkUnits(bean.getUnits());
        CommonConditions conditions = new CommonConditions();
        BeanUtils.copyProperties(bean, conditions, CommonConditions.COMMON_CONDITION_UNITS);
        conditions.setUnits(units);
        return conditionsRepository.save(conditions);
    }

    private Units checkUnits(Long units) throws BadRequestException {
        return unitsRepository.findById(units).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect units id %s", units)));
    }

    public CommonConditions putCommonCondition(Long commonConditionId, CommonConditionsBean bean) throws BadRequestException {
        CommonConditions conditions = checkCommonCondition(commonConditionId);
        Units units = checkUnits(bean.getUnits());
        BeanUtils.copyProperties(bean,conditions,CommonConditions.COMMON_CONDITION_UNITS);
        conditions.setUnits(units);
        return conditionsRepository.save(conditions);
    }
}
