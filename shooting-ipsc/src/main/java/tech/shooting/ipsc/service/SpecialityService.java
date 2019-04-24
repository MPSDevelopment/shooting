package tech.shooting.ipsc.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.SpecialityBean;
import tech.shooting.ipsc.pojo.Speciality;
import tech.shooting.ipsc.repository.SpecialityRepository;

import java.util.List;

@Service
public class SpecialityService {

    @Autowired
    private SpecialityRepository specialityRepository;


    public List<Speciality> getAll() {
        List<Speciality> all = specialityRepository.findAll();
        return all;
    }

    public Speciality speciality(Long specialityId) throws BadRequestException {
        return checkSpeciality(specialityId);
    }

    private Speciality checkSpeciality(Long specialityId) throws BadRequestException {
        return specialityRepository.findById(specialityId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect input id of speciality, check id is %s",specialityId)));
    }

    public Speciality createSpeciality(SpecialityBean bean) {
        Speciality speciality = new Speciality();
        BeanUtils.copyProperties(bean,speciality);
        return specialityRepository.save(speciality);
    }
}
