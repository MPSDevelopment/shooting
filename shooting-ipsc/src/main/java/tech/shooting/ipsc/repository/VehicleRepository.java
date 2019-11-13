package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.pojo.VehicleType;

import java.util.List;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle,Long>, CustomVehicleRepository {
	
	Vehicle findBySerialNumber(String serial);
    
    List<Vehicle> findByOwner(Person person);
    
    List<Vehicle> findByOwnerIn(List<Person> list);
    
    Page<Vehicle> findByOwnerIn(List<Person> persons, PageRequest pageable);
    
    List<Vehicle> findByOwnerAndType(Person person, VehicleType type);
    
    List<Vehicle> findByOwnerAndTypeId(Person person, Long typeId);
    
    long countByOwnerAndTypeId(Person person, Long typeId);
}
