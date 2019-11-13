package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.pojo.CommunicationEquipmentType;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.pojo.VehicleType;

import java.util.List;

@Repository
public interface CommunicationEquipmentRepository extends MongoRepository<CommunicationEquipment,Long>, CustomCommunicationEquipmentRepository {
	
	CommunicationEquipment findBySerialNumber(String serial);
    
    List<CommunicationEquipment> findByOwner(Person person);
    
    List<CommunicationEquipment> findByOwnerIn(List<Person> list);
    
    Page<CommunicationEquipment> findByOwnerIn(List<Person> persons, PageRequest pageable);
    
    List<CommunicationEquipment> findByOwnerAndType(Person person, CommunicationEquipmentType type);
    
    List<CommunicationEquipment> findByOwnerAndTypeId(Person person, Long typeId);
    
    long countByOwnerAndTypeId(Person person, Long typeId);
}
