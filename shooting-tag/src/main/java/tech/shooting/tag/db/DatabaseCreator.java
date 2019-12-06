package tech.shooting.tag.db;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.pojo.Settings;
import tech.shooting.tag.repository.SettingsRepository;

@Component
@Slf4j
public class DatabaseCreator {

	public static final String DEFAULT_SETTINGS_NAME = "Default";

	@Autowired
	private DatabaseCreator databaseCreator;

	@Autowired
	private SettingsRepository appSettingsRepository;

	public DatabaseCreator() {
	}

	@PostConstruct
	public void init() {
		databaseCreator.createDatabase();
	}

	private void createDatabase () {
		if (appSettingsRepository.findByName(DEFAULT_SETTINGS_NAME) == null) {
			appSettingsRepository.save(new Settings().setName(DEFAULT_SETTINGS_NAME).setTagServiceIp("127.0.0.1"));
		}
		
	}
}
