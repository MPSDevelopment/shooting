package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Settings;

@Repository
public interface SettingsRepository extends MongoRepository<Settings, Long> {

	Settings findByName(String name);
}
