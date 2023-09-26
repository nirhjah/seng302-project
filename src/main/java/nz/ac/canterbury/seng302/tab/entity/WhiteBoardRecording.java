package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.helper.VideoType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasVideo;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;


@Entity(name = "WhiteBoardRecordingEntity")
public class WhiteBoardRecording implements Identifiable, HasVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    String name;

    @Enumerated
    private VideoType videoType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

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
}
