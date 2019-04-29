package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.ipsc.repository.CategoriesRepository;
import tech.shooting.ipsc.repository.StandardRepository;
import tech.shooting.ipsc.repository.UnitsRepository;

@Service
public class StandardService {
    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private UnitsRepository unitsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
}
