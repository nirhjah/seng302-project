package nz.ac.canterbury.seng302.tab.service.image;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.helper.ImageService;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ClubImageService extends ImageService<Club> {

    @Autowired
    private ClubService clubService;

    private final byte[] defaultClubLogo;

    /**
     * Writes files to the /{profile}/TEAM_PROFILE_PICTURES/ folder
     * inside the project's directory
     * @param profile The deployment environment, which determines the
     */
    public ClubImageService(@Value("${spring.profiles.active:unknown}") String profile) throws IOException {
        super(getDeploymentType(profile));

        Resource resource = new ClassPathResource("/static/image/icons/club-logo.svg");
        InputStream is = resource.getInputStream();
        defaultClubLogo = is.readAllBytes();
    }

    @Override
    public ImageType getDefaultImageType() {
        return ImageType.SVG;
    }

    @Override
    public String getFolderName() {
        return "CLUB_LOGOS";
    }

    @Override
    public byte[] getDefaultBytes() {
        return defaultClubLogo;
    }

    /**
     * Updates a club logo
     *
     * @param club The club's id
     * @param file The file that represents the image
     */
    public void updateClubLogo(Club club, MultipartFile file) {
        long id = club.getClubId();
        if (clubService.findClubById(id).isPresent()) {
            saveImage(club, file);
            clubService.updateOrAddClub(club);
        }

    }

    public ResponseEntity<byte[]> getImageResponse(long id) {
        Optional<Club> optClub = clubService.findClubById(id);
        if (optClub.isPresent()) {
            return getImageResponse(optClub.get());
        }
        return ResponseEntity.noContent().build();
    }
}
