package tech.shooting.ipsc.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TileService {

	private static final String FOLDER_NAME = "data/tiles/";

	public void createTiles(int row, int column, int zoom, String filename) {
		try {

			new File(FOLDER_NAME).mkdirs();

			BufferedImage originalImgage = ImageIO.read(new File(filename));

			// total width and total height of an image
			int tWidth = originalImgage.getWidth();
			int tHeight = originalImgage.getHeight();

			log.info("Image Dimension: " + tWidth + "x" + tHeight);

			// width and height of each piece
			int eWidth = tWidth / column;
			int eHeight = tHeight / row;

			int x = 0;
			int y = 0;

			for (int i = 0; i < row; i++) {
				y = 0;
				for (int j = 0; j < column; j++) {
					try {

						String extension = FilenameUtils.getExtension(filename);
						File outputfile = getTileImage( filename, i, j, zoom);

						log.info("Creating tile: " + i + " " + j + " " + filename);

						BufferedImage SubImgage = originalImgage.getSubimage(y, x, eWidth, eHeight);
						ImageIO.write(SubImgage, extension, outputfile);

						y += eWidth;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				x += eHeight;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getTileImage(String filename, int tileX, int tileY, int zoom) {
		String extension = FilenameUtils.getExtension(filename);
		String filenameNoExtension = FilenameUtils.getBaseName(filename);
		return new File(FOLDER_NAME + filenameNoExtension + "-" + "z" + zoom + "x" + tileX + "y" + tileY + "." + extension);
	}
}
