package tech.shooting.ipsc.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TileService {

	private static final int TILE_SIZE = 256;
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
						File outputfile = getTileImage(filename, i, j, zoom);

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
	
	public File createTile(String filename, int tileX, int tileY, int zoom) throws IOException {
		return resizeImage(filename, getTileImage(filename, tileX, tileY, zoom), TILE_SIZE, TILE_SIZE);
	}

	/**
	 * Resizes an image to a absolute width and height (the image may not be
	 * proportional)
	 */
	public File resizeImage(String filename, File outputFile, int scaledWidth, int scaledHeight) throws IOException {
		// reads input image
		File inputFile = new File(filename);
		BufferedImage inputImage = ImageIO.read(inputFile);

		// creates output image
		BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
		g2d.dispose();

		// extracts extension of output file
		String formatName = FilenameUtils.getExtension(filename);

		// writes to output file
		ImageIO.write(outputImage, formatName, outputFile);
		
		return outputFile;
	}
	
	

	public byte[] writeImageTobyteArray(BufferedImage image) throws IOException {

		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		ImageIO.write(image, "png", byteArrayOS);

		log.info("Writing image to ByteArray (Length - %s)", byteArrayOS.size());

		return byteArrayOS.toByteArray();

	}

}
