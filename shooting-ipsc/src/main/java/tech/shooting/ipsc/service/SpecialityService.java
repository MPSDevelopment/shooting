package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
