package tech.shooting.ipsc.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tech.shooting.ipsc.service.TileService;

@Controller
@RequestMapping(ControllerAPI.MAP_CONTROLLER)
@Api(ControllerAPI.MAP_CONTROLLER)
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class MapController {

	@Autowired
	private TileService tileService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.MAP_CONTROLLER_GET_TILE_URL, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	@ApiOperation(value = "Get Tile Image", notes = "Returns tile image by x, y and zoom")
	public ResponseEntity<byte[]> getTile(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ID) String id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_Z) int zoom, @PathVariable(value = ControllerAPI.PATH_VARIABLE_X) int tileX, @PathVariable(value = ControllerAPI.PATH_VARIABLE_Y) int tileY) {
		return getTileAsByteArray(id, zoom, tileX, tileY);
	}

	private ResponseEntity<byte[]> getTileAsByteArray(String id, int zoom, int tileX, int tileY) {
        try {
            byte[] body = FileUtils.readFileToByteArray(tileService.getTileImage(id, tileX, tileY, zoom));
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES)).body(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
}
