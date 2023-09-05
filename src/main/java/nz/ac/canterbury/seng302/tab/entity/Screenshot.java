package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import nz.ac.canterbury.seng302.tab.service.image.ScreenshotService;

import java.util.Optional;

@Entity(name = "Screenshot")
public class Screenshot implements Identifiable, HasImage {

    /**
     * OK: this is VERY hacky, but I genuinely couldn't find a better
     * way to do this.
     * When a screenshot entity is deleted, we need to ensure that it's
     * image is actually removed from the filesystem.
     * So, we tag onto the @PostRemove callback from JPA.
     * The trouble is, is that entities can't *really* access services
     * from within themselves.
     * This also isn't ideal, since @PostRemove doesn't guarantee
     * that the txn won't be rolled back:
     * <a href="https://stackoverflow.com/questions/38561246/is-postremove-out-of-transactiont">...</a>
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<ScreenshotService> screenshotService = Optional.empty();

    /**
     * This is terrible, but I couldn't find a better way
     * @param service The screenshotService to inject
     */
    public static void injectScreenshotService(ScreenshotService service) {
        screenshotService = Optional.of(service);
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated
    private ImageType screenshotType;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public ImageType getImageType() {
        return screenshotType;
    }

    @Override
    public void setImageType(ImageType imageType) {
        assert imageType == ImageType.PNG_OR_JPEG : "Screenshots must be PNG or JPEG!";
        screenshotType = imageType;
    }

    /**
     * Clean up screenshot files after entity is deleted.
     * This is quite hacky, see screenshotService for explanation.
     */
    @PostRemove
    public void screenshotDeleted() {
        if (screenshotService.isPresent()) {
            ScreenshotService service = screenshotService.get();
            service.deleteScreenshot(this);
        }
    }
}
