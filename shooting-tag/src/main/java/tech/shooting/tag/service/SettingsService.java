package tech.shooting.tag.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.shooting.tag.pojo.Settings;
import tech.shooting.tag.repository.SettingsRepository;

@Service
public class SettingsService {

	@Autowired
	private SettingsRepository repository;

	public Settings getSettings() {
		return repository.getSettings();
	}

	public Settings putSettings(Settings settings) {
		var result = repository.save(settings);
		return result;
	}
}
