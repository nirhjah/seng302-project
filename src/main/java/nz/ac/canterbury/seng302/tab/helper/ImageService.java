package nz.ac.canterbury.seng302.tab.helper;

import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

public abstract class ImageService<T extends Identifiable & HasImage> extends FileDataSaver {
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
        return builder
                .contentType(MediaType.IMAGE_JPEG)
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
            case SVG -> getImageResponse(imageData);
            case PNG_OR_JPEG -> getSVGResponse(imageData);
        };
    }

    /**
     * Gets an image for an entity.
     * For example, team profile picture, user profile picture, club logo.
     *
     * @param entity The entity to get the image for
     * @return The ResponseEntity to be used with @ResponseBody
     */
    public ResponseEntity<byte[]> getImageResponse(T entity) {
        long id = entity.getId();
        ImageType type = entity.getImageType();

        Optional<byte[]> optImageData = readFile(id);

        if (optImageData.isPresent()) {
            // The entity has an image! Return the data
            return getResponseFromData(optImageData.get(), type);
        } else {
            // No file found, or IO failed. Return default image.
            return getResponseFromData(getDefaultBytes(), getDefaultImageType());
        }
    }

    Set<String> PNGS = Set.of("png", "jpg", "jpeg");
    Set<String> SVGS = Set.of("svg");

    public void setImageTypeOf(T entity, MultipartFile multipartFile) {
        Optional<String> optExtension = getExtension(multipartFile.getName());
        if (optExtension.isPresent()) {
            String extension = optExtension.get().toLowerCase();
            if (PNGS.contains(extension)) {
                entity.setImageType(ImageType.PNG_OR_JPEG);
            } else if (SVGS.contains(extension)){
                entity.setImageType(ImageType.SVG);
            }
        }
    }
}
