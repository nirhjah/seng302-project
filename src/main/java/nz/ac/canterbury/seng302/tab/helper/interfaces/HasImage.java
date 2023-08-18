package nz.ac.canterbury.seng302.tab.helper.interfaces;

import nz.ac.canterbury.seng302.tab.helper.ImageType;

public interface HasImage {
    /**
     * Returns what image type is owned by this entity.
     * Could be SVGs, or PNGs / JPEGs, for example.
     * (Note that JPG, JPEG, and PNG are very similar internally,
     * and will all be displayed appropriately under just JPG.)
     * @return Image type (currently only 2 supported)
     */
    ImageType getImageType();

    void setImageType(ImageType imageType);
}
