package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Categories;

@Repository
public interface CategoriesRepository extends MongoRepository<Categories,Long> {
}
