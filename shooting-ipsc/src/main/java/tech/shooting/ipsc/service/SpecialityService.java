package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.ipsc.repository.SpecialityRepository;

@Service
public class SpecialityService {

    @Autowired
    private SpecialityRepository specialityRepository;


}
