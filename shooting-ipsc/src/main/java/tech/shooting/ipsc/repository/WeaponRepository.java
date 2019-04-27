package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weapon;

import java.util.List;

@Repository
public interface WeaponRepository extends MongoRepository<Weapon,Long> {
    Weapon findBySerialNumber(String serial);
    List<Weapon> findAllByDivision(Division division);
    List<Weapon> findAllByOwner(Person person);
}
