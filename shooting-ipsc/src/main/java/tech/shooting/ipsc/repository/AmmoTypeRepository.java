package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.AmmoType;

@Repository
public interface AmmoTypeRepository extends MongoRepository<AmmoType, Long> {

	AmmoType findByName(String name);
}
