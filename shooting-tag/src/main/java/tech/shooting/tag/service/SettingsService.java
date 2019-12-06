package tech.shooting.tag.service;

import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.tag.db.DatabaseCreator;
import tech.shooting.tag.event.TagRestartEvent;
import tech.shooting.tag.pojo.Settings;
import tech.shooting.tag.repository.SettingsRepository;

@Service
public class SettingsService {

	@Autowired
	private SettingsRepository repository;

	public Settings getSettings() {
		return repository.findByName(DatabaseCreator.DEFAULT_SETTINGS_NAME);
	}

	public Settings putSettings(Settings settings) {
		var result = getSettings();
		if (result == null) {
			result = repository.save(settings.setName(DatabaseCreator.DEFAULT_SETTINGS_NAME));
			EventBus.publishEvent(new TagRestartEvent(settings.getTagServiceIp()));
			return result;
		}
		if (!Objects.equals(settings.getTagServiceIp(), result.getTagServiceIp())) {
			BeanUtils.copyProperties(settings, result, Settings.NAME_FIELD, Settings.ID_FIELD);
			result = repository.save(result);
			EventBus.publishEvent(new TagRestartEvent(settings.getTagServiceIp()));
		}
		return result;
	}
}
