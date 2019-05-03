package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
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

    public Categories getCategoryById(Long categoryId) throws BadRequestException {
        return checkCategory(categoryId);
    }

    private Categories checkCategory(Long categoryId)  throws BadRequestException {
        return categoriesRepository.findById(categoryId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect id category %s",categoryId)));
    }

    public Categories postCategory(Categories categories) {
        return categoriesRepository.save(categories);
    }

    public Categories putCategory(Long categoryId, Categories categories) throws BadRequestException {
        Categories categoriesFromDb = checkCategory(categoryId);
        categoriesFromDb.setNameCategoryKz(categories.getNameCategoryKz());
        categoriesFromDb.setNameCategoryRus(categories.getNameCategoryRus());
        return categoriesRepository.save(categoriesFromDb);
    }

    public void deleteCategory(Long categoryId) throws BadRequestException {
      categoriesRepository.delete(checkCategory(categoryId));
    }
}
