package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;

import java.util.List;

@Repository
public interface WeaponRepository extends MongoRepository<Weapon,Long>, CustomWeaponRepository {
	
    Weapon findBySerialNumber(String serial);
    
    List<Weapon> findByOwner(Person person);
    
    List<Weapon> findByOwnerIn(List<Person> list);
    
    Page<Weapon> findByOwnerIn(List<Person> persons, PageRequest pageable);
    
    List<Weapon> findByOwnerAndWeaponType(Person person, WeaponType weaponType);
    
    List<Weapon> findByOwnerAndWeaponTypeId(Person person, Long weaponTypeId);
    
    long countByOwnerAndWeaponTypeId(Person person, Long weaponTypeId);
}
