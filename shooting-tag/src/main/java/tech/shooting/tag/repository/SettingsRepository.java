package tech.shooting.tag.repository;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.pojo.Settings;
import tech.shooting.tag.utils.JacksonUtils;

@Component
@Slf4j
public class SettingsRepository {

	private static final String FOLDER_NAME = "data";

	private static final String FILE_NAME = "data/config.json";

	public Settings getSettings() {
		var file = checkFile();
		return JacksonUtils.fromJson(Settings.class, file);

	}

	private File checkFile() {
		var folder = new File(FOLDER_NAME);
		var file = new File(FILE_NAME);
		if (!folder.exists()) {
			folder.mkdirs();
			try {
				file = new File(FILE_NAME);
				file.createNewFile();
			} catch (IOException e) {
				log.error("Cannot create settings file %s", FILE_NAME);
			}
		}

		return file;
	}

	public Settings save(Settings settings) {
		var file = checkFile();
		JacksonUtils.writeJsonToFile(JacksonUtils.getJson(settings), FILE_NAME);
		return settings;
	}
}
