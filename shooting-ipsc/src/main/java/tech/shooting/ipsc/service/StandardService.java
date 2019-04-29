package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.pojo.Categories;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.pojo.Units;
import tech.shooting.ipsc.repository.CategoriesRepository;
import tech.shooting.ipsc.repository.StandardRepository;
import tech.shooting.ipsc.repository.SubjectRepository;
import tech.shooting.ipsc.repository.UnitsRepository;

import java.util.List;

@Service
public class StandardService {
    @Autowired
    private StandardRepository standardRepository;

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

    private Standard checkStandard(Long standardId)throws BadRequestException{
        return standardRepository.findById(standardId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect standard id %s ", standardId)));
    }

    public List<Categories> getCategories() {
        return categoriesRepository.findAll();
    }

    public List<Units> getUnits() {
        return unitsRepository.findAll();
    }
}
