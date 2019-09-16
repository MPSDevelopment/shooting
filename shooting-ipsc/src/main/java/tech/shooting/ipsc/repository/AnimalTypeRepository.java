package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.AnimalTypeBean;
import tech.shooting.ipsc.pojo.AnimalType;

@Repository
public interface AnimalTypeRepository extends MongoRepository<AnimalType, Long>, CustomAnimalTypeRepository {

	AnimalType findByName(AnimalTypeBean bean);
}
