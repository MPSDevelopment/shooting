package tech.shooting.ipsc.service;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TileServiceTest {
	
	private TileService service = new TileService();

	@Test
	public void createTiles() {
		
		
			// Provide number of rows and column
			int row = 4;
			int column = 3;
			String filename = "files/Hawaii.png";
			
			service.createTiles(row, column, filename);
			
	}
}
