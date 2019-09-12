package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.CommEquipmentTypeBean;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.CommunicationEquipmentType;
import tech.shooting.ipsc.pojo.WeaponType;


@Repository
public interface CommunicationEquipmentTypeRepository extends MongoRepository<CommunicationEquipmentType,Long> {
	
	CommunicationEquipmentType findByName(CommEquipmentTypeBean bean);
}
