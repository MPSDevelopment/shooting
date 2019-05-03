package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.ipsc.pojo.Categories;
import tech.shooting.ipsc.repository.CategoriesRepository;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Categories> getCategories() {
        return categoriesRepository.findAll();
    }
}
