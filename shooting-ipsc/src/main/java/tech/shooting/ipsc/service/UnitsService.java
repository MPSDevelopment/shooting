package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.UnitBean;
import tech.shooting.ipsc.pojo.Units;
import tech.shooting.ipsc.repository.UnitsRepository;

import java.util.List;

@Service
public class UnitsService {
    @Autowired
    private UnitsRepository unitsRepository;

    public List<Units> getUnits() {
        return unitsRepository.findAll();
    }

    public Units getUnit(Long unitId) throws BadRequestException {
        return checkUnit(unitId);
    }

    private Units checkUnit(Long unitId) throws BadRequestException {
        return unitsRepository.findById(unitId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect unit id %s", unitId)));
    }

    public void deleteUnit(Long unitId) {
        unitsRepository.deleteById(unitId);
    }

    public Units postUnit(UnitBean bean) {
        return unitsRepository.save(new Units().setUnits(bean.getName()));
    }
}
