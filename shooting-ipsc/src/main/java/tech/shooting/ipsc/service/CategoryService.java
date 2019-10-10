//package tech.shooting.ipsc.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import tech.shooting.commons.exception.BadRequestException;
//import tech.shooting.commons.pojo.ErrorMessage;
//import tech.shooting.ipsc.pojo.Category;
//import tech.shooting.ipsc.repository.CategoriesRepository;
//
//import java.util.List;
//
//@Service
//public class CategoryService {
//
//    @Autowired
//    private CategoriesRepository categoriesRepository;
//
//    public List<Category> getCategories() {
//        return categoriesRepository.findAll();
//    }
//
//    public Category getCategoryById(Long categoryId) throws BadRequestException {
//        return checkCategory(categoryId);
//    }
//
//    private Category checkCategory(Long categoryId)  throws BadRequestException {
//        return categoriesRepository.findById(categoryId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect id category %s",categoryId)));
//    }
//
//    public Category postCategory(Category categories) {
//        return categoriesRepository.save(categories);
//    }
//
//    public Category putCategory(Long categoryId, Category categories) throws BadRequestException {
//        Category categoriesFromDb = checkCategory(categoryId);
//        categoriesFromDb.setNameCategoryKz(categories.getNameCategoryKz());
//        categoriesFromDb.setNameCategoryRus(categories.getNameCategoryRus());
//        return categoriesRepository.save(categoriesFromDb);
//    }
//
//    public void deleteCategory(Long categoryId) throws BadRequestException {
//      categoriesRepository.delete(checkCategory(categoryId));
//    }
//}
