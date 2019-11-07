package tech.shooting.ipsc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;

@Service
@Slf4j
public class MapService {

	@Autowired
	private ImageService imageService;

	@Autowired
	private TileService tileService;
	
	private static final String FOLDER_NAME = "data/";

	public byte[] getTileAsByteArray(String filename, int zoom, int tileX, int tileY) throws IOException, BadRequestException, NotFoundException {
		var file = tileService.getTileImage(filename, tileX, tileY, zoom);
		if (file.exists()) {
			log.info("Reading tile from cache %s", file.getAbsolutePath());
			return FileUtils.readFileToByteArray(file);
		}
		var resource = imageService.findResource(filename);
		
		file = new File(FOLDER_NAME + filename);
		FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(file));
		
		return FileUtils.readFileToByteArray(file);
	}

}
