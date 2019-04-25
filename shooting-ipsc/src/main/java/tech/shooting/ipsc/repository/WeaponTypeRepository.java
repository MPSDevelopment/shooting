package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.WeaponType;


@Repository
public interface WeaponTypeRepository extends MongoRepository<WeaponType,Long> {
   WeaponType findByName(WeaponTypeBean bean);
}
