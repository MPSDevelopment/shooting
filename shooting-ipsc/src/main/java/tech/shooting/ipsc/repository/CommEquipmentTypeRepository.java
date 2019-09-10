package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.CommEquipmentTypeBean;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.CommEquipmentType;
import tech.shooting.ipsc.pojo.WeaponType;


@Repository
public interface CommEquipmentTypeRepository extends MongoRepository<CommEquipmentType,Long> {
	
	CommEquipmentType findByName(CommEquipmentTypeBean bean);
}
