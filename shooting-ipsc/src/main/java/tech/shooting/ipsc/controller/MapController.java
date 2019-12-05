package tech.shooting.ipsc.controller;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mpsdevelopment.plasticine.commons.IdGenerator;

import tech.shooting.ipsc.bean.UploadMapBean;
import tech.shooting.ipsc.pojo.Image;
import tech.shooting.ipsc.service.MapService;
import tech.shooting.ipsc.service.TileService;

@Controller
@RequestMapping(ControllerAPI.MAP_CONTROLLER)
@Api(ControllerAPI.MAP_CONTROLLER)
@Slf4j
public class MapController {

	@Autowired
	private TileService tileService;

	@Autowired
	private MapService mapService;
	
	private Tika tika = new Tika();

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.MAP_CONTROLLER_GET_TILE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	@ApiOperation(value = "Get Tile Image", notes = "Returns tile image by x, y and zoom")
	public ResponseEntity<byte[]> getTile(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ID) String id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_Z) int zoom, @PathVariable(value = ControllerAPI.PATH_VARIABLE_X) int tileX,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_Y) int tileY) {
		return getTileAsByteArray(id, zoom, tileX, tileY);
	}

	private ResponseEntity<byte[]> getTileAsByteArray(String id, int zoom, int tileX, int tileY) {
		try {
			File tileImage = tileService.getTileImage(id, tileX, tileY, zoom);
			
			if (!tileImage.exists()) {
				log.error("Tile %s does not exist", tileImage.getAbsolutePath());
			}
			
			byte[] body = FileUtils.readFileToByteArray(tileImage);
			
			// return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES)).body(body);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(tika.detect(tileImage))).body(body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	@PostMapping(value = ControllerAPI.VERSION_1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Post New Map")
	public ResponseEntity<UploadMapBean> postMap(@RequestParam("file") MultipartFile file) throws IOException {
		String nextId = String.valueOf(IdGenerator.nextId());
		Image image = mapService.saveMap(file, nextId + "." + mapService.getExtension(file));
		return ResponseEntity.ok().body(new UploadMapBean(nextId, image.getFileName(), "Map %s has been uploaded", image.getFileName()));
	}

}
