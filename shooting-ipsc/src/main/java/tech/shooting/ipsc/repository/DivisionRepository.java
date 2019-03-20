package tech.shooting.ipsc.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Division;

@Repository
public interface DivisionRepository extends MongoRepository<Division, Long> {
	Division findByNameAndParent (String name, Long parent);

	Division findOneByParent (Division division);
}
