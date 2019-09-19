package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Equipment;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment,Long>, CustomEquipmentRepository {
	
	Equipment findBySerialNumber(String serial);
    
    List<Equipment> findByOwner(Person person);
    
    List<Equipment> findByOwnerIn(List<Person> list);
    
    Page<Equipment> findByOwnerIn(List<Person> persons, PageRequest pageable);
}
