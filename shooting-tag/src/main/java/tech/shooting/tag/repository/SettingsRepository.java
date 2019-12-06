package tech.shooting.tag.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.tag.pojo.Settings;

@Repository
public interface SettingsRepository extends CrudRepository<Settings, Long> {

	Settings findByName(String name);
}
