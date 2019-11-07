package tech.shooting.ipsc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;
import tech.shooting.ipsc.pojo.Image;

@Service
@Slf4j
public class MapService {

	@Autowired
	private ImageService imageService;

	@Autowired
	private TileService tileService;

	private static final String FOLDER_NAME = "data/maps/";

	public File getTileAsFile(String filename, int zoom, int tileX, int tileY) throws IOException, BadRequestException, NotFoundException {
		var file = tileService.getTileImage(filename, tileX, tileY, zoom);
		if (file.exists()) {
			log.info("Reading tile from cache %s", file.getAbsolutePath());
			return file;
		}
		var resource = imageService.findResource(filename);

		file = new File(FOLDER_NAME + filename);
		FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(file));
		return file;

	}

	public byte[] getTileAsByteArray(String filename, int zoom, int tileX, int tileY) throws IOException, BadRequestException, NotFoundException {
		return FileUtils.readFileToByteArray(getTileAsFile(filename, zoom, tileX, tileY));
	}

	public Image saveMap(MultipartFile file, String filename) throws IOException {

		new File(FOLDER_NAME).mkdirs();

		var image = imageService.storeFile(file, filename);

		imageService.findResourceAsFile(filename, FOLDER_NAME + filename);

		tileService.createTiles(10, 10, 10, FOLDER_NAME + filename);

		return image;
	}

	public void clearMap(String filename) throws IOException {
		String destinationFolder = FOLDER_NAME + filename;
		new File(destinationFolder).delete();

		tileService.clearTiles(filename);
	}

}
