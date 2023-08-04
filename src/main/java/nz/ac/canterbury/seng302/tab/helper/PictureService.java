package nz.ac.canterbury.seng302.tab.helper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public abstract class PictureService<T> extends FileDataSaver {
    /**
     * Creates a FileDataSaver.
     * deploymentType is PROD when on production,
     * otherwise, deploymentType is TEST.
     *
     * @param deploymentType   the deploymentType
     * @param fileRestrictions
     */
    protected PictureService(DeploymentType deploymentType, FileRestrictions fileRestrictions) {
        super(deploymentType, fileRestrictions);
    }

    /**
     * Returns what image type is owned by this entity.
     * Could be SVGs, or PNGs, for example.
     * @param entity
     * @return
     */
    public abstract MediaType getImageType(T entity);

    public ResponseEntity<byte[]> getResponse(T entity) {
    }

}
