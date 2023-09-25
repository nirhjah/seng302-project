package nz.ac.canterbury.seng302.tab.service.video;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;
import nz.ac.canterbury.seng302.tab.repository.WhiteBoardRecordingRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WhiteboardRecordingService extends VideoService<WhiteBoardRecording> {
    private static final String FOLDER_NAME = "WHITEBOARD_MEDIA_RECORDINGS";

    private final WhiteBoardRecordingRepository repository;
    private final UserService userService;


    /**
     * Writes files to the /{profile}/WHITEBOARD_MEDIA_RECORDINGS/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    @Autowired
    public WhiteboardRecordingService(@Value("${spring.profiles.active:unknown}") String profile, WhiteBoardRecordingRepository repository, UserService userService) {
        super(getDeploymentType(profile));
        this.userService = userService;
        this.repository = repository;
    }

    @Override
    public String getFolderName() {
        return FOLDER_NAME;
    }


    /*
    We need @Transactional to get around LazyInitializationException.
     https://stackoverflow.com/questions/21574236/how-to-fix-org-hibernate-lazyinitializationexception-could-not-initialize-prox
     */
    @Transactional
    public ResponseEntity<byte[]> getRecording(long id) {
        Optional<WhiteBoardRecording> opt = repository.findById(id);
        if (opt.isPresent()) {
            WhiteBoardRecording recording = opt.get();
            if (canView(recording)) {
                return getVideoResponse(recording);
            } else {
                logger.info("Does not have permission to view: {}", id);
            }
        }
        // else, there's no content:
        logger.info("Failed!!! id: {}", id);
        return ResponseEntity.noContent().build();
    }

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
