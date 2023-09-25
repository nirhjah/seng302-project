package nz.ac.canterbury.seng302.tab.service.video;

import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.VideoType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasVideo;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class VideoService<Entity extends Identifiable & HasVideo> extends FileDataSaver {

    Logger logger = LoggerFactory.getLogger(VideoService.class);

    private static final Map<String, VideoType> videoTypes = Map.of(
            "ogg", VideoType.OGG,
            "mp4", VideoType.MP4,
            "webm", VideoType.WEBM
    );

    // Max of 300mb seems reasonable
    private static final int MAX_SIZE = 300_000_000;

    private static final FileRestrictions DEFAULT_VIDEO_RESTRICTIONS = new FileRestrictions(
            MAX_SIZE, videoTypes.keySet()
    );

    public VideoService(DeploymentType deploymentType) {
        super(deploymentType, DEFAULT_VIDEO_RESTRICTIONS);
    }

    private ResponseEntity<byte[]> getOGGResponse(byte[] videoData) {
        HttpHeaders
    }

    private ResponseEntity<byte[]> getMP4Response(byte[] videoData) {

    }

    private ResponseEntity<byte[]> getWEBMResponse(byte[] videoData) {

    }



    public ResponseEntity<byte[]> getVideoResponse(Entity entity) {
        Optional<byte[]> videoData = readFile(entity.getId());
        if (videoData.isPresent()) {

        }
    }

    private Optional<VideoType> getVideoType(String extension) {
        extension = extension.toLowerCase();
        if (videoTypes.containsKey(extension)) {
            return Optional.of(videoTypes.get(extension));
        }
        return Optional.empty();
    }

    public void saveVideo(Entity entity, MultipartFile multipartFile) {
        Optional<String> optExtension = getExtension(multipartFile);
        if (optExtension.isEmpty()) {
            logger.error("Video file had no extension, couldn't save");
            return;
        }

        String extension = optExtension.get();
        Optional<VideoType> videoType = getVideoType(extension);
        if (videoType.isPresent()) {
            entity.setVideoType(videoType.get());
            boolean ok = saveFile(entity.getId(), multipartFile);
            if (!ok) {
                logger.error("Couldn't save file: {}", entity.getId());
            }
        }
    }
}
