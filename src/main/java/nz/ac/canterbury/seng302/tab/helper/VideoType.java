package nz.ac.canterbury.seng302.tab.helper;

import java.util.Optional;

/**
 * A set of supported VideoTypes.
 */
public enum VideoType {
    MP4,
    WEBM,
    OGG;

    public static Optional<VideoType> getVideoType(String extension) {
        for (VideoType videoType: VideoType.values()) {
            if (extension.equalsIgnoreCase(videoType.name())) {
                return Optional.of(videoType);
            }
        }
        return Optional.empty();
    }

    public String getExtension() {
        return this.name().toLowerCase();
    }
}

