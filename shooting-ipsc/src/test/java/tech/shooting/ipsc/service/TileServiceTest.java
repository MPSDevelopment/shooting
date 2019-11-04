package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TileServiceTest {

	private static final int ZOOM = 10;
	
	private static final String FILENAME = "files/Hawaii.png";

	private TileService service = new TileService();

	@Test
	public void createTiles() {
		// Provide number of rows and column
		int row = 4;
		int column = 3;

		service.createTiles(row, column, ZOOM, FILENAME);
	}

	@Test
	public void getTile() {
		File tileImage = service.getTileImage(FILENAME, 1, 1, 10);
		assertEquals("Hawaii-z10x1y1.png", tileImage.getName());
	}
	
	
	@Test
	public void resizeImage() throws IOException {
		File tileImage = service.resizeImage(FILENAME, new File("data/SmallHawaii.png"), 256, 256);
		assertTrue(tileImage.exists());
	}
	
	@Test
	public void createTile() throws IOException {
		File tileImage = service.createTile(FILENAME, 7, 7, 10);
		assertTrue(tileImage.exists());
	}
}
