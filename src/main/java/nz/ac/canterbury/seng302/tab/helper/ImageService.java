package nz.ac.canterbury.seng302.tab.helper;

import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

/**
 * Provides an abstract class for saving images.
 * @param <Entity> The JPA Entity type. For example, Club, User, Team.
 */
public abstract class ImageService<Entity extends Identifiable & HasImage> extends FileDataSaver {
    Logger logger = LoggerFactory.getLogger(ImageService.class);

    /**
     * Default Ctor for an ImageService.
     * deploymentType is PROD when on production,
     * otherwise, deploymentType is TEST.
     *
     * @param deploymentType   the deploymentType
     */
    protected ImageService(DeploymentType deploymentType) {
        super(deploymentType, DEFAULT_IMAGE_RESTRICTIONS);
    }

    /**
     * The ImageType of the default image.
     * Recall that `getDefaultBytes()` is provided by the FileDataSaver class.
     * @return The imageType.
     */
    public abstract ImageType getDefaultImageType();

    /**
     * Returns an SVG response, given imageData.
     * @param imageData The imagedata in bytes
     * @return a ResponseEntity that will be displayed on the webpage.
     */
    private ResponseEntity<byte[]> getSVGResponse(byte[] imageData) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/svg+xml");
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        return builder
                .headers(headers)
                .body(imageData);
    }

    /**
     * Gets a regular image response (jpg, png, or jpeg)
     * @param imageData The imagedata in bytes
     * @return a ResponseEntity that will be displayed on the webpage.
     */
    private ResponseEntity<byte[]> getImageResponse(byte[] imageData) {
        // It's a regular png/jpg
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return builder
                .contentType(MediaType.IMAGE_JPEG)
                .headers(headers)
                .body(imageData);
    }

    /**
     * Gets a ResponseEntity response, given imageData and a type.
     * @param imageData The imageData in question
     * @param type The ImageType
     * @return The ResponseEntity to be displayed to the user, through @ResponseBody
     */
    private ResponseEntity<byte[]> getResponseFromData(byte[] imageData, ImageType type) {
        return switch (type) {
            case SVG -> getSVGResponse(imageData);
            case PNG_OR_JPEG -> getImageResponse(imageData);
        };
    }

    /**
     * Gets an image for an entity.
     * For example, team profile picture, user profile picture, club logo.
     *
     * @param entity The entity to get the image for
     * @return The ResponseEntity to be used with @ResponseBody
     */
    public ResponseEntity<byte[]> getImageResponse(Entity entity) {
        long id = entity.getId();
        ImageType type = entity.getImageType();

        Optional<byte[]> optImageData = readFile(id);

        if (optImageData.isPresent()) {
            // The entity has an image! Return the data
            if (type == null) {
                type = getDefaultImageType();
            }
            return getResponseFromData(optImageData.get(), type);
        } else {
            // No file found, or IO failed. Return default image.
            return getResponseFromData(getDefaultBytes(), getDefaultImageType());
        }
    }

    // The set of extensions that are encoded like pngs.
    Set<String> PNGS = Set.of("png", "jpg", "jpeg");

    // The set of extensions that are encoded like SVGs.
    Set<String> SVGS = Set.of("svg");

    /**
     * @param entity The JPA entity to save the image for
     * @param multipartFile The MultiPartFile containing the image data
     */
    public void saveImage(Entity entity, MultipartFile multipartFile) {
        Optional<String> optExtension = getExtension(multipartFile);
        if (optExtension.isPresent()) {
            String extension = optExtension.get().toLowerCase();
            boolean ok = false;
            if (PNGS.contains(extension)) {
                entity.setImageType(ImageType.PNG_OR_JPEG);
                ok = saveFile(entity.getId(), multipartFile);
            } else if (SVGS.contains(extension)){
                entity.setImageType(ImageType.SVG);
                ok = saveFile(entity.getId(), multipartFile);
            }

            if (!ok) {
                logger.error("Couldn't save file: {}", entity.getId());
            }
        } else {
            logger.error("Image file had no extension, couldn't save");
        }
    }

    protected void deleteImage(Entity entity) {
        boolean ok = deleteFile(entity.getId());
        if (!ok) {
            logger.error("Couldn't delete file with id: {}", entity.getId());
        }
    }
}
