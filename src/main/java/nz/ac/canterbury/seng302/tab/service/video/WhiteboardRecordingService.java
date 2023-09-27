package nz.ac.canterbury.seng302.tab.service.video;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.repository.WhiteBoardRecordingRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class WhiteboardRecordingService extends VideoService<WhiteBoardRecording> {
    private static final String FOLDER_NAME = "WHITEBOARD_MEDIA_RECORDINGS";

    private final WhiteBoardRecordingRepository repository;

    private final UserService userService;

    private final TeamService teamService;


    /**
     * Writes files to the /{profile}/WHITEBOARD_MEDIA_RECORDINGS/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    @Autowired
    public WhiteboardRecordingService(@Value("${spring.profiles.active:unknown}") String profile, WhiteBoardRecordingRepository repository, UserService userService, TeamService teamService) {
        super(getDeploymentType(profile));
        this.userService = userService;
        this.teamService = teamService;
        this.repository = repository;
    }

    @Override
    public String getFolderName() {
        return FOLDER_NAME;
    }

    /*
    We need @Transactional to get around LazyInitializationException.
     https://stackoverflow.com/questions/21574236/how-to-fix-org-hibernate-lazyinitializationexception-could-not-initialize-prox
    (The same issue occurs in WhiteBoardScreenshotService.)
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

    private WhiteBoardRecording createRecording(MultipartFile file, boolean isPublic) {
        WhiteBoardRecording recording = new WhiteBoardRecording();
        recording.setPublic(isPublic);
        recording = repository.save(recording); // We must save here so the entity is given an id.
        saveVideo(recording, file);
        // We must save again is because the entity is mutated by saveVideo.
        return repository.save(recording);
    }

    public void createRecordingForTeam(MultipartFile file, Team team, boolean isPublic) {
        WhiteBoardRecording recording = createRecording(file, isPublic);
        recording.setTeam(team);
        recording = repository.save(recording);
        team.addRecording(recording);
        teamService.updateTeam(team);
    }


    /**
     * Gets a page of public whiteboard recordings 
     * @param pageable Page size, number, and sorting info
     * @param currentSearch Search by name
     * @param sports The list of sports that the whiteboard's owner (team) can be part of
     * @return A page of whiteboard recordings
     */
    public Page<WhiteBoardRecording> findPublicPaginatedWhiteboardsBySports(
            Pageable pageable,
            String currentSearch,
            List<String> sports) {
        // Nulling the search term makes ignoring it easier
        if (currentSearch != null && currentSearch.isBlank()) {
            currentSearch = null;
        }
        // NOT nulling the list is easier
        if (sports == null) {
            sports = List.of();
        }
        return repository.findPublicWhiteboardsByNameAndSport(pageable, currentSearch, sports);
    }
}
