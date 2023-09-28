package nz.ac.canterbury.seng302.tab.service.image;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.repository.WhiteBoardRecordingRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@Service
public class WhiteboardThumbnailService extends ImageService<WhiteBoardRecording> {

    private final WhiteBoardRecordingRepository repository;

    private final UserService userService;

    protected WhiteboardThumbnailService(@Value("${spring.profiles.active:unknown}") String profile, WhiteBoardRecordingRepository repository, UserService userService) {
        super(getDeploymentType(profile));
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public String getFolderName() {
        return "WHITEBOARD_THUMBNAILS";
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.PNG_OR_JPEG;
    }

    /**
     * Saves the custom thumbnail for a recording
     * @param file the incoming image for screenshot
     * @param recording the whiteboard recording for which thumbnail is being saved
     * @throws Exception thrown if user is not a coach or manager or if recording is invalid
     */
    public void saveThumbnail(MultipartFile file, WhiteBoardRecording recording) throws Exception {
        Optional<User> optU = userService.getCurrentUser();
        if (recording.getId() != 0 && optU.isPresent()) {
            User u = optU.get();
            if (recording.getTeam().isManagerOrCoach(u)) {
                saveImage(recording, file);
                repository.save(recording);
            } else {
                throw new IllegalAccessException();
            }
        } else {
            throw new Exception();
        }
    }

    /**
     * Returns the thumbnail for the whiteboard recording
     * @param id id of the recording
     * @return thumbnail
     */
    @Transactional
    public ResponseEntity<byte[]> getThumbnail(long id) {
        Optional<WhiteBoardRecording> opt = repository.findById(id);
        if (opt.isPresent() && canView(opt.get())) {
            return getImageResponse(opt.get());
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Validates viewing/returning of a thumbnail
     * @param recording the recording that's being accessed
     * @return true if recording is public or if the user is a member of the team
     */
    public boolean canView(WhiteBoardRecording recording) {
        if (recording.isPublic()) {
            return true;
        }

        // Else, loop through all teams that this user is a part of.
        // If the recording belongs to any of the teams,
        // then return true, (else false.)
        Team team = recording.getTeam();
        User user = userService.getCurrentUser().orElseThrow();
        return (team.getTeamMembers().contains(user));
    }
}
