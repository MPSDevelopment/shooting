package tech.shooting.ipsc.controller;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.NotFoundException;
import tech.shooting.ipsc.bean.UploadFileBean;
import tech.shooting.ipsc.pojo.FilePointer;
import tech.shooting.ipsc.pojo.Image;
import tech.shooting.ipsc.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping(ControllerAPI.IMAGE_CONTROLLER)
@Api(value = ControllerAPI.IMAGE_CONTROLLER)
@RestController
@Slf4j
public class ImageController {

	@Autowired
	private ImageService imageService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = ControllerAPI.VERSION_1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Post New Image")
	public ResponseEntity<UploadFileBean> postImage(@RequestParam("file") MultipartFile file) throws IOException {
		Image image = imageService.storeFile(file, String.valueOf(IdGenerator.nextId()));
		return ResponseEntity.ok().body(new UploadFileBean(image.getFileName(), "File %s has been uploaded", image.getFileName()));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + "/" + ControllerAPI.REQUEST_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Post New Image By Filename")
	public ResponseEntity<UploadFileBean> postImageByFileName(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) throws IOException {
		log.info("Trying to Post New Image By Filename by id %s", id);
		Image image = imageService.storeFile(file, id);
		return ResponseEntity.ok().body(new UploadFileBean(image.getFileName(), "File %s has been uploaded", image.getFileName()));
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + "/" + ControllerAPI.REQUEST_ID, produces = MediaType.ALL_VALUE)
	@ApiOperation(value = "Get Image By Filename")
	@ResponseBody
	public ResponseEntity<Resource> getImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String filename) {
		return imageService.findFile(filename).map(file -> prepareResponse(file, response)).orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + "/" + ControllerAPI.REQUEST_ID, produces = MediaType.ALL_VALUE)
	@ApiOperation(value = "Delete Image By Filename")
	@ResponseBody
	public ResponseEntity<Resource> deleteImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String filename) {
		imageService.deleteFile(filename);
		return new ResponseEntity<>(OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get Image Data By Filename")
	public ResponseEntity<Image> getImageData(@PathVariable("id") String filename) throws BadRequestException, NotFoundException {
		return new ResponseEntity<>(imageService.getImageByFilename(filename), OK);
	}

	private ResponseEntity<Resource> prepareResponse(FilePointer filePointer, HttpServletResponse response) {
		return serveDownload(filePointer);
	}

	private ResponseEntity<Resource> serveDownload(FilePointer filePointer) {
//        log.info("status: 200");
		return response(filePointer, OK, filePointer.open());
	}

	private ResponseEntity<Resource> response(FilePointer filePointer, HttpStatus status, Resource body) {

		final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(status).eTag(filePointer.getEtag()).contentLength(filePointer.getSize()).lastModified(filePointer.getLastModified().toEpochMilli());
		filePointer.getMediaType().map(this::toMediaType).ifPresent(responseBuilder::contentType);

		log.info("Content type is %s ", filePointer.getMediaType().get());

		return responseBuilder.body(body);
	}

	private MediaType toMediaType(com.google.common.net.MediaType input) {
		return input.charset().transform(c -> new MediaType(input.type(), input.subtype(), c)).or(new MediaType(input.type(), input.subtype()));
	}
}
