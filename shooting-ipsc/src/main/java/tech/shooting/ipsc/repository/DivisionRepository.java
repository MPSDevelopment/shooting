package tech.shooting.ipsc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Division;

@Repository
public interface DivisionRepository extends MongoRepository<Division, Long>, CustomDivisionRepository {

	Division findByNameAndParent(String name, Long parent);

	Optional<Division> findByParentIsNull();

	Division findOneByParent(Division division);
}
