package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Weapon;

@Repository
public interface WeaponRepository extends MongoRepository<Weapon,Long> {
    Weapon findBySerialNumber(String serial);
    Weapon findAllByDivision(Division division);
}
