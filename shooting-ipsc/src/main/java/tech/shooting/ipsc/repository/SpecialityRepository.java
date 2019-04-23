package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Speciality;

@Repository
public interface SpecialityRepository extends MongoRepository<Speciality,Long> {
}
