package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.VideoType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasVideo;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;


@Entity(name = "WhiteBoardRecordingEntity")
public class WhiteBoardRecording implements Identifiable, HasVideo, HasImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Column
    private String name;

    @Enumerated
    private VideoType videoType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    private ImageType thumbnailType;

    // Private by default.
    @Column
    private boolean isPublic = false;

    @Override
    public long getId() {
        return id;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Changes whether a whiteboard screenshot is private/public
     * @param isPublic true if public, false otherwise
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public VideoType getVideoType() {
        return videoType;
    }
    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public WhiteBoardRecording() {}

    public WhiteBoardRecording(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    @Override
    public ImageType getImageType() {
        return thumbnailType;
    }

    @Override
    public void setImageType(ImageType imageType) {
        this.thumbnailType = imageType;
    }

    @Override
    public String toString() {
        return String.format(
            "WhiteBoardRecording{id=%d, name='%s', team='%s'}", id, name, team
        );
    }
}
