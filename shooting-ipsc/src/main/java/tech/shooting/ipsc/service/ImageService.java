package tech.shooting.ipsc.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.pojo.FilePointer;
import tech.shooting.ipsc.pojo.Image;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	private Tika tika = new Tika();

	public int getCount() {
		int count = 0;
		for (var item : gridFsTemplate.find(new Query())) {
			count++;
		}
		return count;
	}

	public Image getImageByFilename(String filename) throws BadRequestException {
		Optional.ofNullable(filename).orElseThrow(() -> new BadRequestException(new ErrorMessage("Image's filename is null")));
		GridFSFile gridFsFile = Optional.ofNullable(gridFsTemplate.findOne(new Query(Criteria.where("filename").is(filename))))
				.orElseThrow(() -> new BadRequestException(new ErrorMessage(String.format("Image with name %s not found", filename))));
		return JacksonUtils.fromJson(Image.class, JacksonUtils.getJson(gridFsFile.getMetadata()).replace("_id", "id"));
	}

	public Optional<FilePointer> findFile(String filename) {
		GridFsResource resource = gridFsTemplate.getResource(filename);
		if (resource == null) {
			return Optional.empty();
		}
		try {
			// if does it throw IllegalStateException it means that the resource was deleted but still not null
			resource.getContentType();
			return Optional.of(new FilePointer(resource));
		} catch (IllegalStateException e) {
			return Optional.empty();
		}
	}

	public void deleteFile(String filename) {
		GridFsResource resource = gridFsTemplate.getResource(filename);
		if (resource == null) {
			return;
		}
		gridFsTemplate.delete(new Query(Criteria.where("filename").is(filename)));

	}

	public Image storeFile(MultipartFile file, String filename) throws IOException {
		fileIsEmpty(file);
		Image image = new Image(filename, FilenameUtils.getExtension(file.getOriginalFilename()));
		try (InputStream inputStream = file.getInputStream()) {
			gridFsTemplate.delete(Query.query(GridFsCriteria.whereFilename().is(image.getFileName())));
			String id = gridFsTemplate.store(inputStream, image.getFileName(), file.getContentType(), image).toString();
			log.info("Saved image - id is %s; name is %s; Content type is %s", id, filename, file.getContentType());
		}
		return image;
	}

	public Image storeCircularFile(MultipartFile file, String filename) throws IOException {
		fileIsEmpty(file);
		InputStream circleImageInputStream;
		BufferedImage originalImage = ImageIO.read(file.getInputStream());
		int width = originalImage.getWidth() < originalImage.getHeight() ? originalImage.getWidth() : originalImage.getHeight();
		// get a circular cropped image in circleImage
		BufferedImage circleImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = circleImage.createGraphics();
		g2.setClip(new Ellipse2D.Float(0, 0, width, width));
		g2.drawImage(originalImage, 0, 0, width, width, null);
		// make an InputStream for a circleImage
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ImageIO.write(circleImage, "png", os);
			os.flush();
			circleImageInputStream = new ByteArrayInputStream(os.toByteArray());
		}
		Image image = new Image(filename, FilenameUtils.getExtension(file.getOriginalFilename()));
		try (InputStream inputStream = circleImageInputStream) {
			gridFsTemplate.delete(Query.query(GridFsCriteria.whereFilename().is(image.getFileName())));
			String id = gridFsTemplate.store(inputStream, image.getFileName(), file.getContentType(), image).toString();
			log.info("Saved circular image - id is %s; name is %s", id, filename);
		}
		return image;
	}

	private void fileIsEmpty(MultipartFile file) {
		if (file.isEmpty()) {
			throw new ValidationException("File", "Failed to upload. File is empty!");
		}

	}

}
