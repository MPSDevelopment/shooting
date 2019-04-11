package tech.shooting.ipsc.controller;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.IF_MODIFIED_SINCE;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping(ControllerAPI.IMAGE_CONTROLLER)
@Api(value = ControllerAPI.IMAGE_CONTROLLER)
@Controller
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = ControllerAPI.VERSION_1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Post New Image", notes = "Post New Image")
    public ResponseEntity<UploadFileBean> postImage(@RequestParam("file") MultipartFile file) throws IOException {
        Image image = imageService.storeFile(file, String.valueOf(IdGenerator.nextId()));
        return ResponseEntity.ok().body(new UploadFileBean(image.getFileName(), "File %s has been uploaded", image.getFileName()));
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + "/" + ControllerAPI.REQUEST_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Post New Image By Filename", notes = "Post New Image By Filename")
    public ResponseEntity<UploadFileBean> postImageByFileName(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Trying to Post New Image By Filename by id %s", id);
        Image image = imageService.storeFile(file, id);
        return ResponseEntity.ok().body(new UploadFileBean(image.getFileName(), "File %s has been uploaded", image.getFileName()));
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + "/" + ControllerAPI.REQUEST_ID, produces = MediaType.ALL_VALUE)
    @ApiOperation(value = "Get Image By Filename", notes = "Get Image By Filename")
    @ResponseBody
    public ResponseEntity<Resource> getImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String filename,
                                             @ApiParam(IF_NONE_MATCH) @RequestHeader(value = IF_NONE_MATCH, required = false) String requestEtag,
                                             @ApiParam(IF_MODIFIED_SINCE) @RequestHeader(value = IF_MODIFIED_SINCE, required = false) String ifModifiedSince) {
//        Enumeration headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String key = (String) headerNames.nextElement();
//            String value = request.getHeader(key);
//            log.info("key: %s", key);
//            log.info("value: %s", value);
//        }
        requestEtag = request.getHeader(IF_NONE_MATCH);
        ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);
//        log.info("filename: %s;", filename);
//        log.info("requestEtag: %s;", requestEtag);
//        log.info("ifModifiedSince: %s;", ifModifiedSince);
        Optional<String> requestEtagOpt = Optional.ofNullable(requestEtag);
        Optional<String> ifModifiedSinceOpt = Optional.ofNullable(ifModifiedSince);
        return imageService.findFile(filename).map(file -> prepareResponse(file, requestEtagOpt, ifModifiedSinceOpt.map(date -> OffsetDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME)), response)).orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.IMAGE_CONTROLLER_GET_DATA, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get Image Data By Filename", notes = "Get Image Data By Filename")
    public ResponseEntity<Image> getImageData(@PathVariable("id") String filename) throws BadRequestException {
        return new ResponseEntity<>(imageService.getImageByFilename(filename), HttpStatus.OK);
    }

    private ResponseEntity<Resource> prepareResponse(FilePointer filePointer, Optional<String> requestEtagOpt, Optional<OffsetDateTime> ifModifiedSinceOpt, HttpServletResponse response) {
        if (requestEtagOpt.isPresent() && filePointer.matchesEtag(requestEtagOpt.get())) {
            return notModified(response);
        }
        if (ifModifiedSinceOpt.isPresent() && filePointer.modifiedAfter(ifModifiedSinceOpt.get().toInstant())) {
            return notModified(response);
        }
        return serveDownload(filePointer);
    }

    private ResponseEntity<Resource> serveDownload(FilePointer filePointer) {
//        log.info("status: 200");
        return response(filePointer, OK, filePointer.open());
    }

    private ResponseEntity<Resource> notModified(HttpServletResponse response) {
//        log.info("status: 304");
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        return new ResponseEntity<>(NOT_MODIFIED);
        //return response(filePointer, NOT_MODIFIED, null);
    }

    private ResponseEntity<Resource> response(FilePointer filePointer, HttpStatus status, Resource body) {
        final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(status).eTag(filePointer.getEtag()).contentLength(filePointer.getSize()).lastModified(filePointer.getLastModified().toEpochMilli());
        filePointer.getMediaType().map(this::toMediaType).ifPresent(responseBuilder::contentType);
        return responseBuilder.body(body);
    }

    private MediaType toMediaType(com.google.common.net.MediaType input) {
        return input.charset().transform(c -> new MediaType(input.type(), input.subtype(), c)).or(new MediaType(input.type(), input.subtype()));
    }
}
