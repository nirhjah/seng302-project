package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;


@Entity(name = "WhiteboardScreenshotEntity")
public class WhiteboardScreenshot implements Identifiable, HasImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Enumerated
    private ImageType screenshotType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    // Screenshots are private by default.
    // this is safer
    @Column
    private boolean isPublic = false;

    @Column(name="tag")
    private String tag;

    @Override
    public long getId() {
        return id;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String newTag) {
        this.tag = newTag;
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

    /**
     * Changes whether a whiteboard screenshot is private/public
     * @param isPublic true if public, false otherwise
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public ImageType getImageType() {
        return screenshotType;
    }

    @Override
    public void setImageType(ImageType imageType) {
        this.screenshotType = imageType;
    }

    public WhiteboardScreenshot() {}
}
